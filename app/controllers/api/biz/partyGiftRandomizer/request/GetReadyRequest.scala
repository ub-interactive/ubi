package controllers.api.biz.partyGiftRandomizer.request

import play.api.libs.json.{Json, OFormat}

/**
  * @author liangliao at 2018/10/22 11:18 PM
  */
case class GetReadyRequest(
                            id: Long,
                            openid: String,
                            height: String,
                            weight: String
                          )

object GetReadyRequest {
  implicit val format: OFormat[GetReadyRequest] = Json.format[GetReadyRequest]
}