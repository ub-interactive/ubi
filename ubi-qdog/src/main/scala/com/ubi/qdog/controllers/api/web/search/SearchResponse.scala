package com.ubi.qdog.controllers.api.web.search

import java.time.Instant
import java.util.UUID

import com.ubi.qdog.controllers.api.{ApiResponseData, PaginationResponseData}
import com.ubi.qdog.enums.CourseSaleTypeValue
import com.ubi.qdog.entities.CourseEntity.Tags
import play.api.libs.json.{Json, OFormat, OWrites}

final case class SearchResponse(
  courses: Iterable[SearchResponse.Course],
  page: PaginationResponseData
) extends ApiResponseData

object SearchResponse {
  implicit val writes: OWrites[SearchResponse] = Json.writes[SearchResponse]

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





