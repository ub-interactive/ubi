package com.ubi.ccat.services

import com.aliyuncs.http.MethodType
import com.aliyuncs.profile.DefaultProfile
import com.aliyuncs.{CommonRequest, DefaultAcsClient}
import com.ubi.ccat.configs.AliCloudServiceConfig
import com.ubi.ccat.services.ShortMessageService.SmsMessageResponse
import javax.inject.Inject
import play.api.libs.json.{Json, OFormat}

import scala.util.Try

class ShortMessageService @Inject()(aliCloudServiceConfig: AliCloudServiceConfig) {

  val profile: DefaultProfile = DefaultProfile.getProfile(aliCloudServiceConfig.regionId, aliCloudServiceConfig.accessKey, aliCloudServiceConfig.accessSecret)
  val client: DefaultAcsClient = new DefaultAcsClient(profile)

  private def withRequest(process: CommonRequest => CommonRequest): Try[SmsMessageResponse] = {
    val request = new CommonRequest()
    request.setMethod(MethodType.POST)
    request.setDomain("dysmsapi.aliyuncs.com")
    request.setVersion("2017-05-25")
    request.setAction("SendSms")
    request.putQueryParameter("RegionId", aliCloudServiceConfig.regionId)
    request.putQueryParameter("SignName", aliCloudServiceConfig.signName)
    Try(Json.parse(client.getCommonResponse(process(request)).getData).validate[SmsMessageResponse].get)
  }

  def sendSms(message: ShortMessageService.SmsMessage): Try[String] = {
    withRequest { request =>
      request.putQueryParameter("PhoneNumbers", message.phoneNumbers.mkString(","))
      request.putQueryParameter("TemplateCode", message.templateCode)
      request.putQueryParameter("TemplateParam", Json.toJson(message.templateParam).toString())
      request
    }.map(_.BizId)
  }
}

object ShortMessageService {

  sealed trait SmsMessage {
    def phoneNumbers: Seq[String]

    def templateCode: String

    def templateParam: Map[String, String]
  }

  final case class MobileVerifyMessage(
    phoneNumber: String,
    code: String
  ) extends SmsMessage {
    val templateCode = "SMS_186395028"
    val phoneNumbers = Seq(phoneNumber)
    val templateParam = Map("code" -> code)
  }

  final case class SmsMessageResponse(
    Message: String,
    RequestId: String,
    BizId: String,
    Code: String
  )

  object SmsMessageResponse {
    implicit val format: OFormat[SmsMessageResponse] = Json.format[SmsMessageResponse]
  }

}
