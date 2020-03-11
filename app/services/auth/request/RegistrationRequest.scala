package services.auth.request

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsonValidationError, Reads, __}
import services.BaseRequest

/**
  * @author liangliao at 2018/10/10 1:01 AM
  */
case class RegistrationRequest(nationCode: String, identifier: String, name: String, avatar: Option[String]) extends BaseRequest

object RegistrationRequest {
  implicit val signUpReads: Reads[RegistrationRequest] = (
    (__ \ "nationCode").read[String] and
      (__ \ "identifier").read[String].filter(JsonValidationError("请填写有效手机号码"))(_.matches("""^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\d{8}$"""))
      and (__ \ "name").read[String]
      and (__ \ "avatar").readNullable[String]
    ) (RegistrationRequest.apply _)
}