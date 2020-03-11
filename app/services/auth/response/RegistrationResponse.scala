package services.auth.response

import org.joda.time.DateTime
import play.api.libs.json._
import services.BaseResponse

/**
  * @author liangliao at 2018/10/10 1:02 AM
  */
case class RegistrationResponse(token: Option[String] = None,
                                expiresOn: Option[DateTime] = None) extends BaseResponse

object RegistrationResponse {
  implicit val dateTimeWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("dd/MM/yyyy HH:mm:ss")
  implicit val dateTimeJsReader: Reads[DateTime] = JodaReads.jodaDateReads("dd/MM/yyyy HH:mm:ss")

  implicit val format: OFormat[RegistrationResponse] = Json.format[RegistrationResponse]
}