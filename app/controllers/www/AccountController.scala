package controllers.www

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.auth.environments.JWTEnv
import services.auth.{AccountIdentityService, JWTAuthenticateService}
import services.user.TokenService

/**
  * Created by liangliao on 27/6/17.
  */
class AccountController @Inject()(
  silhouette: Silhouette[JWTEnv],
  jWTAuthentication: JWTAuthenticateService,
  configuration: Configuration,
  userActionTokenService: TokenService,
  userIdentityService: AccountIdentityService
) extends InjectedController with I18nSupport {

  def index(): Action[AnyContent] = {
    silhouette.SecuredAction { implicit request: RequestHeader =>
      Ok
    }
  }

  def signIn(returnUrl: String): Action[AnyContent] = {
    silhouette.UserAwareAction { implicit request: RequestHeader =>
      Ok
    }
  }

  def signOut(returnUrl: String): Action[AnyContent] = {
    silhouette.SecuredAction { implicit request: RequestHeader =>
      val protocol = if (request.secure) "https://" else "http://"
      Ok
      //    Redirect(protocol + request.host + returnUrl).discardingCookies(DiscardingCookie(cookieSettings.name, cookieSettings.path, cookieSettings.domain, cookieSettings.secure))
    }
  }
}
