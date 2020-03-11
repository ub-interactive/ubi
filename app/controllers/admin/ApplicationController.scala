package controllers.admin

import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter

class ApplicationController @Inject() extends InjectedController with I18nSupport {

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("AdminRoutes")(
      )
    ).as("text/javascript")
  }

}
