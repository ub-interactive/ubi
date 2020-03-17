package com.ubi.ccat.services

import akka.Done
import com.aliyuncs.http.MethodType
import com.aliyuncs.profile.DefaultProfile
import com.aliyuncs.{CommonRequest, DefaultAcsClient}
import com.ubi.ccat.configs.AliCloudServiceConfig
import com.ubi.ccat.services.ShortMessageService.SmsMessageResponse
import javax.inject.Inject
import play.api.libs.json.{Json, OFormat}
import redis.RedisClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Random, Success, Try}

class ShortMessageService @Inject()(
  aliCloudServiceConfig: AliCloudServiceConfig,
  redisClient: RedisClient
)
  (implicit executionContext: ExecutionContext) {

  val profile: DefaultProfile = DefaultProfile.getProfile(aliCloudServiceConfig.regionId, aliCloudServiceConfig.accessKey, aliCloudServiceConfig.accessSecret)
  val client: DefaultAcsClient = new DefaultAcsClient(profile)

  def sendSms(message: ShortMessageService.SmsMessage): Try[String] = {
    withRequest { request =>
      request.putQueryParameter("PhoneNumbers", message.phoneNumbers.mkString(","))
      request.putQueryParameter("TemplateCode", message.templateCode)
      request.putQueryParameter("TemplateParam", Json.toJson(message.templateParam).toString())
      request
    }.map(_.BizId)
  }


  private def mobileVerificationKey(mobile: String): String = {
    s"MOBILE_VERIFICATION_$mobile"
  }

  def sendMobileVerificationMessage(mobile: String): Future[Done.type] = {
    val key = mobileVerificationKey(mobile)
    for {
      codeOpt <- redisClient.get[String](key)
      _ <- codeOpt match {
        case Some(_) => Future.failed(new IllegalStateException("mobile.verification.too.fast"))
        case None =>
          val code: String = Random.between(100000, 1000000).toString
          redisClient.set(key, code, Some(5.minute.toSeconds)).flatMap {
            case true => sendSms(ShortMessageService.MobileVerifyMessage(
              phoneNumber = mobile,
              code = code
            )) match {
              case Failure(exception) => Future.failed(exception)
              case Success(value) => Future.successful(value)
            }
            case false => Future.failed(new IllegalStateException("failed.to.cache.token"))
          }
      }
    } yield Done
  }

  def verifyMobile(
    mobile: String,
    code: String
  ): Future[Boolean] = {
    val key = mobileVerificationKey(mobile)
    redisClient.get[String](key).map {
      case Some(value) if value == code => true
      case None => false
    }
  }

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
