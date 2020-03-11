package controllers.api.auth

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import controllers.api.ApiErrorHandler
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc._
import services.auth._
import services.auth.environments.JWTEnv
import services.auth.models.JWTToken
import services.auth.request.AuthRequest
import services.auth.response.AuthResponse

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by liangliao on 27/6/17.
  */
class AuthenticationController @Inject()(silhouette: Silhouette[JWTEnv],
                                         jWTAuthenticateService: JWTAuthenticateService,
                                        )(implicit ec: ExecutionContext) extends InjectedController with I18nSupport with ApiErrorHandler {

  def signIn: Action[JsValue] = silhouette.UserAwareAction.async(parse.json) { implicit request =>
    request.body.validate[AuthRequest] map { authRequest =>
      jWTAuthenticateService.authenticate(authRequest) flatMap {
        case Left(_) => Future.successful(Unauthorized(Json.toJson(AuthResponse())))
        case Right(JWTToken(token, expiresOn)) =>
          silhouette.env.authenticatorService.embed(token, Ok(Json.toJson(AuthResponse(token = Some(token), expiresOn = Some(expiresOn)))))
      }
    } catchErrors
  }

  def signOut: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    jWTAuthenticateService.signOut(Ok)
  }

}
