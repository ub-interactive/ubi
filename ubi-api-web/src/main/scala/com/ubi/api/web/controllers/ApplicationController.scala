package com.ubi.api.web.controllers

import akka.Done
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class ApplicationController @Inject()(
  val dbConfigProvider: DatabaseConfigProvider
)
  (implicit val ec: ExecutionContext) extends WebApiController {

  def index: Action[AnyContent] = {
    Action { implicit request =>
      Done.ok
    }
  }
}
