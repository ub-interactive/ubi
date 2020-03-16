package com.ubi.ccat.controllers.api.web

import java.time.Instant

import javax.inject.Inject
import me.chanjar.weixin.mp.api.WxMpService
import play.api.Environment
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext
import scala.util.Try

class WebApplicationController @Inject()(
  environment: Environment,
  wxMpService: WxMpService,
  val dbConfigProvider: DatabaseConfigProvider
)
  (implicit val ec: ExecutionContext) extends WebApiController {

  def getWxJsApiConfig(url: String): Action[AnyContent] = {
    Action { implicit request =>
      Try(wxMpService.createJsapiSignature(url)).toEither match {
        case Left(value) => value.error
        case Right(value) => GetWxJsApiConfigResponse(
          appId = value.getAppId,
          nonceStr = value.getNonceStr,
          timestamp = Instant.ofEpochMilli(value.getTimestamp),
          url = value.getUrl,
          signature = value.getSignature
        ).ok
      }
    }
  }

}
