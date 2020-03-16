package com.ubi.ccat.controllers.api.web.wechat

import com.ubi.ccat.controllers.api.ApiResponseData
import play.api.libs.json.{Json, OFormat}

final case class GetAuthorizationUrlResponse(
  url: String
) extends ApiResponseData

object GetAuthorizationUrlResponse {
  implicit val format: OFormat[GetAuthorizationUrlResponse] = Json.format[GetAuthorizationUrlResponse]
}
