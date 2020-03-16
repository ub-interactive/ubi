package com.ubi.ccat.controllers.api.web.wechat

import com.ubi.ccat.controllers.api.ApiResponseData
import play.api.libs.json.{Json, OFormat}

final case class GetUserInfoResponse(
  openId: String,
  nickname: String,
  sex: Int,
  language: String,
  city: String,
  province: String,
  country: String,
  headImgUrl: String
) extends ApiResponseData

object GetUserInfoResponse {
  implicit val format: OFormat[GetUserInfoResponse] = Json.format[GetUserInfoResponse]
}
