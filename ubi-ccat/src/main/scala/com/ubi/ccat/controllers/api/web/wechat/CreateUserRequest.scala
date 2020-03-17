package com.ubi.ccat.controllers.api.web.wechat

import com.ubi.ccat.controllers.api.ApiRequestData
import play.api.libs.json.{Json, OFormat}

final case class CreateUserRequest(
  openId: String,
  mobile: String
) extends ApiRequestData

object CreateUserRequest {
  implicit val format: OFormat[CreateUserRequest] = Json.format[CreateUserRequest]
}


