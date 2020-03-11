package services.auth

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorResult
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasher}
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.exceptions.{IdentityNotFoundException, InvalidPasswordException}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import daos.user.AccountDao
import entities.user.{AccountEntity, ActionTokenEntity}
import javax.inject.Inject
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc.{RequestHeader, Result}
import services.auth.environments.JWTEnv
import services.auth.models._
import services.auth.request._
import services.auth.response.PasswordUpdateResponse
import services.sys.ImageService
import services.user._

import scala.concurrent.{ExecutionContext, Future}

class JWTAuthenticateService @Inject()(
                                        silhouette: Silhouette[JWTEnv],
                                        accountIdentityService: AccountIdentityService,
                                        tokenService: TokenService,
                                        credentialsProvider: CredentialsProvider,
                                        passwordHasher: PasswordHasher,
                                        authInfoRepository: AuthInfoRepository,
                                        imageService: ImageService,
                                        accountDao: AccountDao,
                                      )(implicit ec: ExecutionContext) {

  def authenticate(authRequest: AuthRequest)(implicit request: RequestHeader): Future[Either[AccountStateError, JWTToken]] = {
    val AuthRequest(identifier, password) = authRequest
    credentialsProvider.authenticate(Credentials(identifier, password)) flatMap { loginInfo =>
      accountIdentityService.retrieve(loginInfo) flatMap accountStatusCheck {
        case Some(account) if account.state == implicitly[String](AccountEntity.State.Activated) => issueJWTToken(loginInfo) map (Right(_))
      }
    } recover {
      case _: InvalidPasswordException => Left(InvalidCredentials)
      case _: IdentityNotFoundException => Left(AccountNotExists$)
    }
  }

  def issueJWTToken(loginInfo: LoginInfo)(implicit request: RequestHeader): Future[JWTToken] = {
    for {
      accountEntityOpt <- accountIdentityService.retrieve(loginInfo)
      accountEntity = accountEntityOpt.get if accountEntityOpt.isDefined
      authenticator <- silhouette.env.authenticatorService.create(loginInfo).map(_.copy(customClaims = Some(Json.obj("user_id" -> accountEntity.id))))
      tokenValue <- silhouette.env.authenticatorService.init(authenticator)
      expiration = authenticator.expirationDateTime
    } yield {
      JWTToken(token = tokenValue, expiresOn = expiration)
    }
  }

  def issueActionToken(loginInfo: LoginInfo, tokenAction: ActionTokenEntity.ActionValue, validForMinutes: Int)(implicit request: RequestHeader): Future[ActionToken] = {
    for {
      accountEntityOpt <- accountIdentityService.retrieve(loginInfo)
      accountEntity = accountEntityOpt.get if accountEntityOpt.isDefined
      result <- tokenService.issue(accountEntity, tokenAction, validForMinutes) map { token =>
        ActionToken(token = token.token, expiresOn = token.expiresOn)
      }
    } yield {
      result
    }
  }

  def consumeActionToken(loginInfo: LoginInfo, action: ActionTokenEntity.ActionValue, token: String): Future[Boolean] = {
    for {
      accountEntityOpt <- accountIdentityService.retrieve(loginInfo)
      accountEntity = accountEntityOpt.get if accountEntityOpt.isDefined
      actionTokenOpt <- tokenService.retrieve(accountEntity, action, token) if actionTokenOpt.nonEmpty && !actionTokenOpt.get.expiresOn.isBefore(DateTime.now)
      actionToken = actionTokenOpt.get if actionTokenOpt.isDefined
      result <- tokenService.consume(accountEntity, actionToken) map (_.nonEmpty)
    } yield {
      result
    }
  }

  def signOut(result: Result)(implicit request: SecuredRequest[JWTEnv, _]): Future[AuthenticatorResult] = {
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }

  def register(registrationRequest: RegistrationRequest, validForMinutes: Int = 30)(implicit request: RequestHeader): Future[Either[AccountStateError, JWTToken]] = {
    val loginInfo = LoginInfo(CredentialsProvider.ID, registrationRequest.identifier)
    accountIdentityService.retrieve(loginInfo) flatMap accountStatusCheck {
      case None =>
        accountDao.transaction {
          for {
            imageEntity <- registrationRequest.avatar map {
              imageService.uploadUrl(_, None)
            } match {
              case Some(f) => f.map(Some(_))
              case None => Future.successful(None)
            }
            accountEntity <- accountDao.create(AccountEntity(name = registrationRequest.name, mobile = Some(registrationRequest.identifier), state = AccountEntity.State.Created, avatarI = imageEntity.map(_.id)))
            _ <- accountIdentityService.create(loginInfo, accountEntity)
            tokenEntity <- tokenService.issue(accountEntity, ActionTokenEntity.Action.ActivateAccount, validForMinutes)
          } yield {
            Right(JWTToken(tokenEntity.token, tokenEntity.expiresOn))
          }
        }
    }
  }

  def updatePassword(passwordUpdateRequest: PasswordUpdateRequest)(implicit request: RequestHeader): Future[Either[AccountStateError, PasswordUpdateResponse]] = {
    val PasswordUpdateRequest(loginInfo, password) = passwordUpdateRequest
    accountIdentityService.retrieve(loginInfo) flatMap accountStatusCheck {
      case Some(_) =>
        val authInfo = passwordHasher.hash(password)
        authInfoRepository.save(loginInfo, authInfo) map (passwordInfo => Right(PasswordUpdateResponse(passwordInfo = passwordInfo)))
    }
  }

  private def accountStatusCheck[T](pf: PartialFunction[Option[AccountEntity], Future[Either[AccountStateError, T]]]): PartialFunction[Option[AccountEntity], Future[Either[AccountStateError, T]]] = {
    val accountStatePf: PartialFunction[Option[AccountEntity], Future[Either[AccountStateError, T]]] = {
      case Some(account) if account.state == implicitly[String](AccountEntity.State.Created) => Future.successful(Left(AccountNotActivated$))
      case Some(account) if account.state == implicitly[String](AccountEntity.State.Activated) => Future.successful(Left(AccountAlreadyExists$))
      case Some(account) if account.state == implicitly[String](AccountEntity.State.Disabled) => Future.successful(Left(AccountDisabled$))
      case None => Future.successful(Left(AccountNotExists$))
    }
    pf orElse accountStatePf
  }


}