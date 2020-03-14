package com.ubi.ccat.controllers.api.web.home

import java.util.UUID

import com.ubi.ccat.controllers.api.ApiResponseData
import com.ubi.ccat.enums.BannerLinkTypeValue
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
