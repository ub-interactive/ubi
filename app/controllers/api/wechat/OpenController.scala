package controllers.api.wechat

import com.google.inject.name.Named
import javax.inject.Inject
import me.chanjar.weixin.common.error.WxErrorException
import me.chanjar.weixin.open.api.WxOpenService
import me.chanjar.weixin.open.bean.message.WxOpenXmlMessage
import org.apache.commons.lang3.StringUtils
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, _}

import scala.xml.NodeSeq

/**
  * Created by liangliao on 7/14/16.
  */
class OpenController @Inject()(
                                wxOpenService: WxOpenService
                              ) extends InjectedController with I18nSupport {

  def preauth(auth_code: Option[String]) = Action { implicit request =>
    auth_code match {
      case None =>
        val redirectUrl = routes.OpenController.preauth(None).absoluteURL()
        Redirect(wxOpenService.getWxOpenComponentService.getPreAuthUrl(redirectUrl))

      case Some(_) =>
        Ok("授权成功!")
    }
  }

  def route(timestamp: String,
            nonce: String,
            signature: String,
            encrypt_type: String,
            msg_signature: String): Action[NodeSeq] = Action(parse.xml) { implicit request =>

    if (!StringUtils.equalsIgnoreCase("aes", encrypt_type) || !wxOpenService.getWxOpenComponentService.checkSignature(timestamp, nonce, signature)) {
      InternalServerError("非法请求，可能属于伪造的请求！")
    } else {
      val inMessage = WxOpenXmlMessage.fromEncryptedXml(request.body.toString(), wxOpenService.getWxOpenConfigStorage, timestamp, nonce, msg_signature)
      Logger.debug(s"\n消息解密后内容为：\n${inMessage.toString} ")
      try {
        val out = wxOpenService.getWxOpenComponentService.route(inMessage)
        Logger.debug("\n组装回复信息：$out")
        Ok(out)
      } catch {
        case e: WxErrorException =>
          Logger.error("receive_ticket", e)
          InternalServerError
      }
    }
  }

}
