package controllers.www

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.auth.environments.JWTEnv

/**
  * Created by liangliao on 27/6/17.
  */
class ApplicationController @Inject()(silhouette: Silhouette[JWTEnv]
                                     ) extends InjectedController with I18nSupport {

  def index(): Action[AnyContent] = silhouette.UserAwareAction { implicit request: RequestHeader =>
    Ok
  }

  def blog(): Action[AnyContent] = silhouette.UserAwareAction { implicit request: RequestHeader =>
    Ok
  }
}
