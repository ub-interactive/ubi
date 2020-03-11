package controllers.api.biz.partyGiftRandomizer.request

import play.api.libs.json.{Json, OFormat}

/**
  * @author liangliao at 2018/10/22 11:17 PM
  */
case class GetRequest(
                       id: Long,
                       openid: String
                     )

object GetRequest {
  implicit val format: OFormat[GetRequest] = Json.format[GetRequest]
}

