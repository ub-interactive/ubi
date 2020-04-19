package com.ubi.qdog.controllers.api.web.user

import com.ubi.qdog.controllers.api.ApiResponseData
import play.api.libs.json.{Json, OFormat}

final case class GetWechatAuthorizationUrlResponse(
  url: String
) extends ApiResponseData

object GetWechatAuthorizationUrlResponse {
  implicit val format: OFormat[GetWechatAuthorizationUrlResponse] = Json.format[GetWechatAuthorizationUrlResponse]
}
