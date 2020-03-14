package com.ubi.ccat.controllers.api.web.subject

import java.time.Instant
import java.util.UUID

import com.ubi.ccat.controllers.api.ApiResponseData
import com.ubi.ccat.controllers.api.web.subject
import com.ubi.ccat.enums.CourseSaleTypeValue
import com.ubi.ccat.persistence.slick.CourseEntity.Tags
import play.api.libs.json.{Json, OFormat}

final case class GetSubjectResponse(
  subjectId: UUID,
  title: String,
  courses: Iterable[subject.GetSubjectResponse.Course]
) extends ApiResponseData

object GetSubjectResponse {
  implicit val format: OFormat[GetSubjectResponse] = Json.format[GetSubjectResponse]

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


