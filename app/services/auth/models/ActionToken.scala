package services.auth.models

import org.joda.time.DateTime
import play.api.libs.json._

/**
  * @author liangliao at 2018/10/10 1:05 AM
  */
case class ActionToken(token: String, expiresOn: DateTime)

object ActionToken {
  implicit val dateTimeJsWriter: Writes[DateTime] = JodaWrites.jodaDateWrites("dd/MM/yyyy HH:mm:ss")
  implicit val dateTimeJsReader: Reads[DateTime] = JodaReads.jodaDateReads("dd/MM/yyyy HH:mm:ss")
  implicit val format: OFormat[ActionToken] = Json.format[ActionToken]
}

