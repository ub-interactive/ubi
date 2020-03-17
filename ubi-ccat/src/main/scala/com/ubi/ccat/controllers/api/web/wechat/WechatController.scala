package com.ubi.ccat.controllers.api.web.wechat

import java.time.Instant

import com.ubi.ccat.controllers.api.ApiRequest
import com.ubi.ccat.controllers.api.web.WebApiController
import com.ubi.crm.api.CrmService
import com.ubi.crm.api.enums.UserGender
import javax.inject.Inject
import me.chanjar.weixin.mp.api.WxMpService
import play.api.Environment
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class WechatController @Inject()(
  environment: Environment,
  crmService: CrmService,
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

  def createUser(): Action[JsValue] = {
    Action.async(parse.json) { implicit request =>
      request.body.validate[ApiRequest[CreateUserRequest]].withData { createUserRequest =>
        val CreateUserRequest(openId, mobile) = createUserRequest
        Try {
          wxMpService.getUserService.userInfo(openId)
        } match {
          case Failure(exception) => Future.successful(exception.error)
          case Success(value) => crmService.userCreate.invoke(com.ubi.crm.api.user.CreateUserRequest(
            mobile = mobile,
            openId = value.getOpenId,
            nickname = value.getNickname,
            gender = value.getSex.intValue() match {
              case 0 => UserGender.Secret
              case 1 => UserGender.Male
              case 2 => UserGender.Female
            },
            language = value.getLanguage,
            city = value.getCity,
            province = value.getProvince,
            country = value.getCity,
            avatarUrl = value.getHeadImgUrl
          )).ok
        }
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

}
