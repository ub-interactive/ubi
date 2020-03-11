package services.auth.request

import com.mohiva.play.silhouette.api.LoginInfo
import play.api.libs.json.{Json, OFormat}
import services.BaseRequest

/**
  * @author liangliao at 2018/10/10 1:00 AM
  */
case class ActionTokenConsumeRequest(loginInfo: LoginInfo, action: String, token: String) extends BaseRequest

object ActionTokenConsumeRequest {
  implicit val format: OFormat[ActionTokenConsumeRequest] = Json.format[ActionTokenConsumeRequest]
}