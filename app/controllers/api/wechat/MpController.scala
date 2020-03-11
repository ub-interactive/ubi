package controllers.api.wechat

import services.auth.environments.JWTEnv
import com.google.inject.name.Named
import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.Inject
import me.chanjar.weixin.mp.api.WxMpService
import play.api.i18n.I18nSupport
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{Action, _}
import play.api.routing.JavaScriptReverseRouter

/**
  * Created by liangliao on 7/14/16.
  */
class MpController @Inject()(silhouette: Silhouette[JWTEnv],
                             wxMpService: WxMpService
                            ) extends InjectedController with I18nSupport {

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("WechatRoutes")(
        controllers.api.wechat.routes.javascript.MpController.jsApiSignature
      )
    ).as("text/javascript")
  }


  def validateApiToken(signature: String, timestamp: String, nonce: String, echostr: String): Action[AnyContent] = Action { implicit request =>
    if (wxMpService.checkSignature(timestamp, nonce, signature)) {
      Ok(echostr)
    } else {
      NotFound
    }
  }

  case class JsApiSignature(appId: String, nonceStr: String, timestamp: Long, url: String, signature: String)

  object JsApiSignature {
    implicit val format: OFormat[JsApiSignature] = Json.format[JsApiSignature]
  }

  def jsApiSignature: Action[AnyContent] = Action { implicit request =>
    request.headers.get("referer") match {
      case Some(referer) =>
        val jsApiSignature = wxMpService.createJsapiSignature(referer)
        Ok(
          Json.toJson(
            JsApiSignature(
              appId = jsApiSignature.getAppId,
              nonceStr = jsApiSignature.getNonceStr,
              timestamp = jsApiSignature.getTimestamp,
              url = jsApiSignature.getUrl,
              signature = jsApiSignature.getSignature
            )))
      case _ => NotFound
    }
  }

}
