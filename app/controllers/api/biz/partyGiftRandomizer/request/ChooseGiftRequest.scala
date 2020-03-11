package controllers.api.biz.partyGiftRandomizer.request

import play.api.libs.json.{Json, OFormat}

/**
  * @author liangliao at 2018/10/22 11:19 PM
  */
case class ChooseGiftRequest(
                              id: Long,
                              openid: String,
                              beneficiaryId: Long,
                              giftId: Long
                            )

object ChooseGiftRequest {
  implicit val format: OFormat[ChooseGiftRequest] = Json.format[ChooseGiftRequest]
}

