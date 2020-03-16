package com.ubi.ccat.controllers.api.web.wechat

import java.time.Instant

import com.ubi.ccat.controllers.api.web.WebApiController
import javax.inject.Inject
import me.chanjar.weixin.mp.api.WxMpService
import play.api.Environment
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

class WechatController @Inject()(
  environment: Environment,
  wxMpService: WxMpService,
  val dbConfigProvider: DatabaseConfigProvider
)
  (implicit val ec: ExecutionContext) extends WebApiController {

  def getAuthorizationUrl(
    redirectUrl: String,
    scope: Option[String],
    state: Option[String]
  ): Action[AnyContent] = {
    Action { implicit request =>
      GetAuthorizationUrlResponse(
        url = wxMpService.oauth2buildAuthorizationUrl(redirectUrl, scope.getOrElse("snsapi_userinfo"), state.getOrElse(""))
      ).ok
    }
  }

  def getUserInfo(code: String): Action[AnyContent] = {
    Action { implicit request =>
      Try {
        val accessToken = wxMpService.oauth2getAccessToken(code)
        wxMpService.oauth2getUserInfo(accessToken, "zh_CN")
      } match {
        case Failure(exception) => exception.error
        case Success(value) => GetUserInfoResponse(
          openId = value.getOpenId,
          nickname = value.getNickname,
          sex = value.getSex,
          language = value.getLanguage,
          city = value.getCity,
          province = value.getProvince,
          country = value.getCountry,
          headImgUrl = value.getHeadImgUrl
        ).ok
      }
    }
  }

  def getWxJsApiConfig(url: String): Action[AnyContent] = {
    Action { implicit request =>
      Try(wxMpService.createJsapiSignature(url)) match {
        case Failure(exception) => exception.error
        case Success(value) => GetJsApiConfigResponse(
          appId = value.getAppId,
          nonceStr = value.getNonceStr,
          timestamp = Instant.ofEpochMilli(value.getTimestamp),
          url = value.getUrl,
          signature = value.getSignature
        ).ok
      }
    }
  }

  //  def getWx = Action{ implicit request =>
  //    wxMpService.getUserService.userInfo()
  //  }

}
