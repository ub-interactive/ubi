package com.ubi.qdog.controllers.api.web.home

import java.util.UUID

import com.ubi.qdog.controllers.api.ApiResponseData
import com.ubi.qdog.enums.BannerLinkTypeValue
import play.api.libs.json.{Json, OFormat}

final case class GetHomeContentResponse(
  banners: Iterable[GetHomeContentResponse.Banner]
) extends ApiResponseData

object GetHomeContentResponse {
  implicit val format: OFormat[GetHomeContentResponse] = Json.format[GetHomeContentResponse]

  final case class Banner(
    coverUrl: String,
    bannerLinkType: BannerLinkTypeValue,
    subjectId: UUID
  )

  object Banner {
    implicit val format: OFormat[Banner] = Json.format[Banner]
  }

}
