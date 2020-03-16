package com.ubi.crm.api

import java.util.UUID

import com.ubi.crm.api.enums.UserGenderValue
import play.api.libs.json.{Json, OFormat}

final case class CreateUserResponse(
  userId: UUID,
  openId: String,
  nickname: String,
  gender: UserGenderValue,
  language: String,
  city: String,
  province: String,
  country: String,
  avatarUrl: String
)

object CreateUserResponse {
  implicit val format: OFormat[CreateUserResponse] = Json.format[CreateUserResponse]
}
