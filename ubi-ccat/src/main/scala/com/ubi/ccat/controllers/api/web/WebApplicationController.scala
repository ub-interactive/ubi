package com.ubi.ccat.controllers.api.web

import java.time.Instant

import javax.inject.Inject
import me.chanjar.weixin.mp.api.WxMpService
import play.api.Environment
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class WebApplicationController @Inject()(
  environment: Environment,
  wxMpService: WxMpService,
  val dbConfigProvider: DatabaseConfigProvider
)
  (implicit val ec: ExecutionContext) extends WebApiController {

  def getWxJsApiConfig(url: String): Action[AnyContent] = {
    Action { implicit request =>
      val jsApiSignature = wxMpService.createJsapiSignature(url)
      GetWxJsApiConfigResponse(
        appId = jsApiSignature.getAppId,
        nonceStr = jsApiSignature.getNonceStr,
        timestamp = Instant.ofEpochMilli(jsApiSignature.getTimestamp),
        url = jsApiSignature.getUrl,
        signature = jsApiSignature.getSignature
      ).ok
    }
  }

}
