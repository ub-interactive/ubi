package controllers

import com.google.inject.name.Named
import javax.inject.Inject
import play.api.Environment
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

class UploadedAssets @Inject()(environment: Environment, @Named("uploadFolder") uploadFolder: String) extends InjectedController with I18nSupport {
  def at(file: String) = Action { implicit request =>
    Try(Ok.sendFile(new java.io.File(environment.rootPath + "/" + uploadFolder + file))).getOrElse(NotFound)
  }
}
