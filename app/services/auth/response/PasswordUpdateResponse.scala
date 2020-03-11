package services.auth.response

import com.mohiva.play.silhouette.api.util.PasswordInfo
import play.api.libs.json.{Json, OFormat}
import services.BaseResponse

/**
  * @author liangliao at 2018/10/10 1:09 AM
  */
case class PasswordUpdateResponse(passwordInfo: PasswordInfo) extends BaseResponse

object PasswordUpdateResponse {
  implicit val passwordInfoFormat: OFormat[PasswordInfo] = Json.format[PasswordInfo]
  implicit val format: OFormat[PasswordUpdateResponse] = Json.format[PasswordUpdateResponse]
}
