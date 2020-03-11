package services.auth.request

import com.mohiva.play.silhouette.api.LoginInfo
import play.api.libs.json.{Json, OFormat}
import services.BaseRequest

/**
  * @author liangliao at 2018/10/10 1:09 AM
  */
case class PasswordUpdateRequest(loginInfo: LoginInfo, newPassword: String) extends BaseRequest

object PasswordUpdateRequest {
  implicit val format: OFormat[PasswordUpdateRequest] = Json.format[PasswordUpdateRequest]
}

