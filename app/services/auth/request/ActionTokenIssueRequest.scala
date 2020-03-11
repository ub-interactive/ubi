package services.auth.request

import com.mohiva.play.silhouette.api.LoginInfo
import play.api.libs.json.{Json, OFormat}
import services.BaseRequest

/**
  * @author liangliao at 2018/10/10 12:59 AM
  */
case class ActionTokenIssueRequest(loginInfo: LoginInfo, action: String, sendSms: Boolean) extends BaseRequest

object ActionTokenIssueRequest {
  implicit val format: OFormat[ActionTokenIssueRequest] = Json.format[ActionTokenIssueRequest]
}
