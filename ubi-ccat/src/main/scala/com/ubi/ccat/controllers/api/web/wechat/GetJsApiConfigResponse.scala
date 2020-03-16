package com.ubi.ccat.controllers.api.web.wechat

import java.time.Instant

import com.ubi.ccat.controllers.api.ApiResponseData
import play.api.libs.json.{Json, OFormat}

final case class GetJsApiConfigResponse(
  appId: String,
  nonceStr: String,
  timestamp: Instant,
  url: String,
  signature: String
) extends ApiResponseData

object GetJsApiConfigResponse {
  implicit val format: OFormat[GetJsApiConfigResponse] = Json.format[GetJsApiConfigResponse]
}


