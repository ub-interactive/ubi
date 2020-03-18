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
import scala.util.Random

class ShortMessageService @Inject()(
  aliCloudServiceConfig: AliCloudServiceConfig,
  redisClient: RedisClient
)
  (implicit executionContext: ExecutionContext) {

  val profile: DefaultProfile = DefaultProfile.getProfile(aliCloudServiceConfig.regionId, aliCloudServiceConfig.accessKey, aliCloudServiceConfig.accessSecret)
  val client: DefaultAcsClient = new DefaultAcsClient(profile)

  private def withRequest(process: CommonRequest => CommonRequest): Future[SmsMessageResponse] = {
    val request = new CommonRequest()
    request.setMethod(MethodType.POST)
    request.setDomain("dysmsapi.aliyuncs.com")
    request.setVersion("2017-05-25")
    request.setAction("SendSms")
    request.putQueryParameter("RegionId", aliCloudServiceConfig.regionId)
    request.putQueryParameter("SignName", aliCloudServiceConfig.signName)

    Future(Json.parse(client.getCommonResponse(process(request)).getData).validate[SmsMessageResponse].get)
  }

  def sendSms(message: ShortMessageService.SmsMessage): Future[String] = {
    withRequest { request =>
      request.putQueryParameter("PhoneNumbers", message.phoneNumbers.mkString(","))
      request.putQueryParameter("TemplateCode", message.templateCode)
      request.putQueryParameter("TemplateParam", Json.toJson(message.templateParam).toString())
      request
    }.map(_.BizId)
  }

  private def getMobileVerificationCodeKey(mobile: String): String = {
    s"MOBILE_VERIFICATION_CODE_$mobile"
  }

  def sendMobileVerificationCode(mobile: String): Future[Done.type] = {
    val semaphonreKey = s"MOBILE_VERIFICATION_SEMAPHORE_$mobile"
    val codeKey = getMobileVerificationCodeKey(mobile)
    redisClient.get(semaphonreKey).flatMap {
      case Some(_) =>
        Future.failed(new IllegalStateException("mobile.verification.too.fast"))
      case None =>
        for {
          _ <- redisClient.set(semaphonreKey, "", Some(1.minute.toSeconds))
          code = Random.between(100000, 1000000).toString
          _ <- redisClient.set(codeKey, code, Some(5.minute.toSeconds))
          _ <- sendSms(ShortMessageService.MobileVerifyMessage(
            phoneNumber = mobile,
            code = code
          ))
        } yield Done
    }
  }

  def verifyMobileCode(
    mobile: String,
    code: String
  ): Future[Boolean] = {
    val key = getMobileVerificationCodeKey(mobile)
    redisClient.get[String](key).flatMap {
      case Some(value) if value == code => redisClient.del(key).map(_ => true)
      case Some(value) if value != code => Future.successful(false)
      case None => Future.successful(false)
    }
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
