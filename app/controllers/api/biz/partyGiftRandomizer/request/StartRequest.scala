package controllers.api.biz.partyGiftRandomizer.request

import play.api.libs.json.{Json, OFormat}

/**
  * @author liangliao at 2018/10/22 11:18 PM
  */
case class StartRequest(
                         id: Long,
                         openid: String
                       )

object StartRequest {
  implicit val format: OFormat[StartRequest] = Json.format[StartRequest]
}