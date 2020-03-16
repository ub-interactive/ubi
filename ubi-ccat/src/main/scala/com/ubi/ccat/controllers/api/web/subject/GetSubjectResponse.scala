package com.ubi.ccat.controllers.api.web.subject

import java.time.Instant
import java.util.UUID

import com.ubi.ccat.controllers.api.web.subject
import com.ubi.ccat.controllers.api.{ApiResponseData, PaginationResponseData}
import com.ubi.ccat.enums.CourseSaleTypeValue
import com.ubi.ccat.tables.CourseEntity.Tags
import play.api.libs.json.{Json, OFormat, OWrites}

final case class GetSubjectResponse(
  subjectId: UUID,
  title: String,
  courses: Iterable[subject.GetSubjectResponse.Course],
  page: PaginationResponseData
) extends ApiResponseData

object GetSubjectResponse {
  implicit val writes: OWrites[GetSubjectResponse] = Json.writes[GetSubjectResponse]

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


