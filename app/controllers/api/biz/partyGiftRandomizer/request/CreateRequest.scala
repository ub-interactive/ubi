package controllers.api.biz.partyGiftRandomizer.request

import play.api.libs.json.{Json, OFormat}

/**
  * @author liangliao at 2018/10/22 11:17 PM
  */
case class CreateRequest(
                          openid: String,
                          nickName: String,
                          avatarUrl: String
                        )

object CreateRequest {
  implicit val format: OFormat[CreateRequest] = Json.format[CreateRequest]
}