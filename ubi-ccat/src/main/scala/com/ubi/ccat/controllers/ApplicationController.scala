package com.ubi.ccat.controllers

import com.ubi.ccat.controllers.api.web.WebApiController
import javax.inject.Inject
import play.api.Environment
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class ApplicationController @Inject()(
  environment: Environment,
  val dbConfigProvider: DatabaseConfigProvider
)
  (implicit val ec: ExecutionContext) extends WebApiController {

  def index: Action[AnyContent] = {
    Action {
      Ok
    }
  }

  def file(path: String): Action[AnyContent] = {
    Action { implicit request =>
      environment.getExistingFile(s"/uploaded/$path") match {
        case Some(value) => Ok.sendFile(value)
        case None => NotFound
      }
    }
  }
}
