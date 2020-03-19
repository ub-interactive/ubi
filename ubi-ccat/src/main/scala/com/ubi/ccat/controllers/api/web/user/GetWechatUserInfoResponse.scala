package com.ubi.ccat.controllers.api.web.user

import com.ubi.ccat.controllers.api.ApiResponseData
import com.ubi.crm.api.enums.UserGenderValue
import play.api.libs.json.{Json, OFormat}

final case class GetWechatUserInfoResponse(
  openId: String,
  nickname: String,
  gender: UserGenderValue,
  language: String,
  city: String,
  province: String,
  country: String,
  avatarUrl: String
) extends ApiResponseData

object GetWechatUserInfoResponse {
  implicit val format: OFormat[GetWechatUserInfoResponse] = Json.format[GetWechatUserInfoResponse]
}
