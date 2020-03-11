package controllers.api.biz.partyGiftRandomizer.request

import play.api.libs.json.{Json, OFormat}

/**
  * @author liangliao at 2018/10/22 11:18 PM
  */
case class JoinRequest(
                        id: Long,
                        openid: String,
                        nickName: String,
                        avatarUrl: String
                      )

object JoinRequest {
  implicit val format: OFormat[JoinRequest] = Json.format[JoinRequest]
}