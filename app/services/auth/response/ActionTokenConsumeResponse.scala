package services.auth.response

import play.api.libs.json.{Json, OFormat}
import services.BaseResponse

/**
  * @author liangliao at 2018/10/10 1:00 AM
  */
case class ActionTokenConsumeResponse(valid: Boolean) extends BaseResponse

object ActionTokenConsumeResponse {
  implicit val format: OFormat[ActionTokenConsumeResponse] = Json.format[ActionTokenConsumeResponse]
}