package com.ubi.crm.api.user

import com.ubi.crm.api.enums.UserGenderValue
import play.api.libs.json.{Json, OFormat}

final case class CreateUserRequest(
  mobile: String,
  openId: String,
  nickname: String,
  gender: UserGenderValue,
  language: String,
  city: String,
  province: String,
  country: String,
  avatarUrl: String
)

object CreateUserRequest {
  implicit val format: OFormat[CreateUserRequest] = Json.format[CreateUserRequest]
}
