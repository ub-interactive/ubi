package controllers.api.biz.partyGiftRandomizer.request

import play.api.libs.json.{Json, OFormat}

/**
  * @author liangliao at 2018/10/22 11:19 PM
  */
case class AddGiftRequest(
  id: Long,
  openid: String,
  giftInfo: String
)

object AddGiftRequest {
  implicit val format: OFormat[AddGiftRequest] = Json.format[AddGiftRequest]
}