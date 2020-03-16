package com.ubi.ccat.controllers.api.web

import java.time.Instant

import com.ubi.ccat.controllers.api.ApiResponseData
import play.api.libs.json.{Json, OFormat}

final case class GetWxJsApiConfigResponse(
  appId: String,
  nonceStr: String,
  timestamp: Instant,
  url: String,
  signature: String
) extends ApiResponseData

object GetWxJsApiConfigResponse {
  implicit val format: OFormat[GetWxJsApiConfigResponse] = Json.format[GetWxJsApiConfigResponse]
}


