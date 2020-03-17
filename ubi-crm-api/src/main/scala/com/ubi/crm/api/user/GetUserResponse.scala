package com.ubi.crm.api.user

import com.ubi.crm.api.enums.UserGenderValue
import play.api.libs.json.{Json, OFormat}

final case class GetUserResponse(
  openId: String,
  nickname: String,
  gender: UserGenderValue,
  language: String,
  city: String,
  province: String,
  country: String,
  avatarUrl: String
)

object GetUserResponse {
  implicit val format: OFormat[GetUserResponse] = Json.format[GetUserResponse]
}


