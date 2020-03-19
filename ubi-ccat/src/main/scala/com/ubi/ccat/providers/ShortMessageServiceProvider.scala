package com.ubi.ccat.providers

import akka.Done
import com.aliyuncs.http.MethodType
import com.aliyuncs.profile.DefaultProfile
import com.aliyuncs.{CommonRequest, DefaultAcsClient}
import com.google.inject.Provider
import com.typesafe.config.Config
import com.ubi.ccat.providers.ShortMessageService.{SmsMessage, SmsMessageResponse}
import javax.inject.Inject
import play.api.libs.json.{Json, OFormat}
import play.api.{ConfigLoader, Configuration}
import redis.RedisClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

trait ShortMessageService {
  def sendMessage(smsMessage: SmsMessage): Future[Done]

  def sendMobileVerificationCode(mobile: String): Future[Done.type]

  def verifyMobileCode(
    mobile: String,
    code: String
  ): Future[Boolean]
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

final case class AliCloudServiceConfig(
  regionId: String,
  accessKey: String,
  accessSecret: String,
  signName: String
)

object AliCloudServiceConfig {
  implicit val configLoader: ConfigLoader[AliCloudServiceConfig] =
    (config: Config, path: String) => {
      val c = Configuration(config.getConfig(path))
      AliCloudServiceConfig(
        regionId = c.get[String]("region-id"),
        accessKey = c.get[String]("access-key"),
        accessSecret = c.get[String]("access-secret"),
        signName = c.get[String]("sign-name")
      )
    }
}

class ShortMessageServiceProvider @Inject()(
  configuration: Configuration,
  redisClient: RedisClient
)
  (implicit executionContext: ExecutionContext) extends Provider[ShortMessageService] {

  private val aliCloudServiceConfig: AliCloudServiceConfig = configuration.get[AliCloudServiceConfig]("ali-cloud-service")

  override def get(): ShortMessageService = {
    new ShortMessageService {

      val profile: DefaultProfile = DefaultProfile.getProfile(aliCloudServiceConfig.regionId, aliCloudServiceConfig.accessKey, aliCloudServiceConfig.accessSecret)
      val client: DefaultAcsClient = new DefaultAcsClient(profile)

      override def sendMessage(smsMessage: SmsMessage): Future[Done] = {
        withRequest { request =>
          request.putQueryParameter("PhoneNumbers", smsMessage.phoneNumbers.mkString(","))
          request.putQueryParameter("TemplateCode", smsMessage.templateCode)
          request.putQueryParameter("TemplateParam", Json.toJson(smsMessage.templateParam).toString())
          request
        }.map(_ => Done)
      }

      private def getMobileVerificationCodeKey(mobile: String): String = {
        s"MOBILE_VERIFICATION_CODE_$mobile"
      }

      override def sendMobileVerificationCode(mobile: String): Future[Done.type] = {
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
              _ <- sendMessage(ShortMessageService.MobileVerifyMessage(
                phoneNumber = mobile,
                code = code
              ))
            } yield Done
        }
      }

      override def verifyMobileCode(
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
    }
  }
}