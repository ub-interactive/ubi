package controllers.api.user

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import controllers.api.CrudController
import daos._
import entities.user.AccountEntity
import filters.user.AccountFilter
import play.api.libs.json
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.mvc.Action
import services.auth._
import services.auth.authorizations.Has
import services.auth.environments.JWTEnv
import services.auth.models.JWTToken
import services.auth.request.RegistrationRequest
import services.auth.response.{AuthResponse, RegistrationResponse}
import services.user.UserService

import scala.concurrent.{ExecutionContext, Future}

class AccountController @Inject()(
  val silhouette: Silhouette[JWTEnv],
  val has: Has,
  val daoService: UserService,
  jWTAuthenticateService: JWTAuthenticateService
)
  (implicit ec: ExecutionContext) extends CrudController[AccountEntity] {

  implicit val entityFormat: json.Format[AccountEntity] = AccountEntity.format
  implicit val filterFormat: OFormat[EntityFilter[AccountEntity]] = AccountFilter.format
  implicit val resultFormat: OFormat[EntityResult[AccountEntity]] = Json.format[EntityResult[AccountEntity]]

  def register: Action[JsValue] = {
    silhouette.UserAwareAction.async(parse.json) { implicit request =>
      request.body.validate[RegistrationRequest] map { registrationRequest: RegistrationRequest =>
        jWTAuthenticateService.register(registrationRequest) flatMap {
          case Left(_) => Future.successful(Ok(Json.toJson(AuthResponse())))
          case Right(JWTToken(token, expiresOn)) => {
            Future.successful(Ok(Json.toJson(RegistrationResponse(Some(token), Some(expiresOn)))))
          }
        }
      } catchErrors
    }
  }
}
