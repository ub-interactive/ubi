package com.ubi.qdog.controllers.api.web.user

import java.time.Instant

import com.ubi.qdog.controllers.api.ApiResponseData
import play.api.libs.json.{Json, OFormat}

final case class GetWechatJsApiConfigResponse(
  appId: String,
  nonceStr: String,
  timestamp: Instant,
  url: String,
  signature: String
) extends ApiResponseData

object GetWechatJsApiConfigResponse {
  implicit val format: OFormat[GetWechatJsApiConfigResponse] = Json.format[GetWechatJsApiConfigResponse]
}


