package com.ubi.ccat.controllers.api.web.home

import java.time.Instant
import java.util.UUID

import com.ubi.ccat.controllers.api.ApiResponseData
import com.ubi.ccat.enums.{BannerLinkTypeValue, CourseSaleTypeValue, SubjectDisplayStyleValue}
import com.ubi.ccat.persistence.slick.CourseEntity.Tags
import play.api.libs.json.{Json, OFormat}

final case class GetHomeContentResponse(
  banners: Iterable[GetHomeContentResponse.Banner],
  subjects: Iterable[GetHomeContentResponse.Subject]
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

  final case class Subject(
    subjectId: UUID,
    title: String,
    displayStyle: SubjectDisplayStyleValue,
    courses: Iterable[Subject.Course]
  )

  object Subject {
    implicit val format: OFormat[Subject] = Json.format[Subject]

    final case class Course(
      courseId: UUID,
      title: String,
      subtitle: Option[String],
      thumbnailUrl: Option[String],
      price: Int,
      promotionPrice: Option[Int],
      saleType: CourseSaleTypeValue,
      tags: Tags,
      flashSaleStartAt: Option[Instant],
      flashSaleEndAt: Option[Instant]
    )

    object Course {
      implicit val format: OFormat[Course] = Json.format[Course]
    }

  }

}
