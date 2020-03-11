package controllers.api.auth

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.services.IdentityService
import controllers.api.ApiErrorHandler
import entities.user.{AccountEntity, ActionTokenEntity}
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc._
import services.auth._
import services.auth.environments.JWTEnv
import services.auth.models.JWTToken
import services.auth.request.{ActionTokenConsumeRequest, ActionTokenIssueRequest, PasswordUpdateRequest}
import services.auth.response.{ActionTokenConsumeResponse, ActionTokenIssueResponse, AuthResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Created by liangliao on 27/6/17.
  */
class ActionTokenController @Inject()(
  silhouette: Silhouette[JWTEnv],
  jWTAuthenticateService: JWTAuthenticateService,
  userIdentityService: IdentityService[AccountEntity]
)
  (implicit ec: ExecutionContext) extends InjectedController with I18nSupport with ApiErrorHandler {

  def issue: Action[JsValue] = {
    silhouette.UserAwareAction.async(parse.json) { implicit request =>
      request.body.validate[ActionTokenIssueRequest] map { smsTokenRequest: ActionTokenIssueRequest =>
        val ActionTokenIssueRequest(loginInfo, action, sendSms) = smsTokenRequest
        for {
          tokenAction <- Future.fromTry(Try(ActionTokenEntity.Action.fromString(action).get))
          actionToken <- jWTAuthenticateService.issueActionToken(loginInfo, tokenAction, 30)
        } yield {
          if (sendSms) {
            Ok
          } else {
            Ok(Json.toJson(ActionTokenIssueResponse(token = actionToken)))
          }
        }
      } catchErrors
    }
  }

  def consume: Action[JsValue] = {
    silhouette.UserAwareAction.async(parse.json) { implicit request =>
      val actionTokenConsumeResponseFailed: Future[Result] = Future.successful(Ok(Json.toJson(ActionTokenConsumeResponse(false))))

      request.body.validate[ActionTokenConsumeRequest] map { tokenConsumeRequest =>
        val ActionTokenConsumeRequest(loginInfo, action, token) = tokenConsumeRequest

        ActionTokenEntity.Action.fromString(action) map { tokenAction =>
          jWTAuthenticateService.consumeActionToken(loginInfo, tokenAction, token) flatMap {
            case true =>
              tokenAction match {
                case ActionTokenEntity.Action.SignIn =>
                  jWTAuthenticateService.issueJWTToken(loginInfo) flatMap {
                    case JWTToken(jwtToken, expiresOn) => silhouette.env.authenticatorService.embed(jwtToken, Ok(Json.toJson(AuthResponse(token = Some(jwtToken), expiresOn = Some(expiresOn)))))
                  }
                case ActionTokenEntity.Action.ResetPassword =>
                  request.body.validate[PasswordUpdateRequest] map { passwordUpdateRequest: PasswordUpdateRequest =>
                    jWTAuthenticateService.updatePassword(passwordUpdateRequest) flatMap {
                      case Left(_) => Future.successful(Ok(Json.toJson(AuthResponse())))
                      case Right(passwordUpdateResponse) => Future.successful(Ok(Json.toJson(passwordUpdateResponse)))
                    }
                  } get
                case _ => actionTokenConsumeResponseFailed
              }
            case _ => actionTokenConsumeResponseFailed
          }
        } getOrElse actionTokenConsumeResponseFailed
      } catchErrors
    }
  }
}
