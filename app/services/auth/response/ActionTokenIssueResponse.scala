package services.auth.response

import play.api.libs.json.{Json, OFormat}
import services.BaseResponse
import services.auth.models.ActionToken

/**
  * @author liangliao at 2018/10/10 1:00 AM
  */
case class ActionTokenIssueResponse(token: ActionToken) extends BaseResponse

object ActionTokenIssueResponse {
  implicit val format: OFormat[ActionTokenIssueResponse] = Json.format[ActionTokenIssueResponse]
}