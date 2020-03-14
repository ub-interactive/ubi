package com.ubi.ccat.controllers

import com.ubi.ccat.controllers.api.web.WebApiController
import controllers.AssetsFinder
import javax.inject.Inject
import play.api.Environment
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}
import play.shaded.oauth.org.apache.commons.codec.digest.DigestUtils

import scala.concurrent.ExecutionContext

class ApplicationController @Inject()(
  environment: Environment,
  as: AssetsFinder,
  val dbConfigProvider: DatabaseConfigProvider
)
  (implicit val ec: ExecutionContext) extends WebApiController {

  def index: Action[AnyContent] = {
    Action { implicit request =>
      Ok
    }
  }

  def file(path: String): Action[AnyContent] = {
    Action { implicit request =>
      environment.getExistingFile(s"/uploaded/$path") match {
        case Some(value) =>
          val eTag = DigestUtils.md5Hex(value.getAbsolutePath + value.lastModified().toString)
          request.headers.get("If-None-Match") match {
            case Some(value) if value == eTag => NotModified
            case None => Ok.sendFile(value).withHeaders(ETAG -> eTag)
          }
        case None => NotFound
      }
    }
  }
}
