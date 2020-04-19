package com.ubi.qdog.controllers.api.web.user

import com.ubi.qdog.controllers.api.ApiRequestData
import play.api.libs.json.{Json, OFormat}

final case class CreateUserRequest(
  openId: String,
  mobile: String
) extends ApiRequestData

object CreateUserRequest {
  implicit val format: OFormat[CreateUserRequest] = Json.format[CreateUserRequest]
}


