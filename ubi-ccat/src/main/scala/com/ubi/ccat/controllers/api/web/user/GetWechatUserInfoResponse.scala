package com.ubi.ccat.controllers.api.web.user

import com.ubi.ccat.controllers.api.ApiResponseData
import play.api.libs.json.{Json, OFormat}

final case class GetWechatUserInfoResponse(
  openId: String,
  nickname: String,
  sex: Int,
  language: String,
  city: String,
  province: String,
  country: String,
  headImgUrl: String
) extends ApiResponseData

object GetWechatUserInfoResponse {
  implicit val format: OFormat[GetWechatUserInfoResponse] = Json.format[GetWechatUserInfoResponse]
}
