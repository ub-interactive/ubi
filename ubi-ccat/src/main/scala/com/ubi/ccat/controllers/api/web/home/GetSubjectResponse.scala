package com.ubi.ccat.controllers.api.web.home

import java.time.Instant
import java.util.UUID

import com.ubi.ccat.controllers.api.{ApiResponseData, PaginationResponseData}
import com.ubi.ccat.enums.{CourseSaleTypeValue, SubjectDisplayStyleValue}
import com.ubi.ccat.entities.CourseEntity.Tags
import play.api.libs.json.{Json, OFormat, OWrites}

final case class GetSubjectResponse(
  subjects: Iterable[GetSubjectResponse.Subject],
  page: PaginationResponseData
) extends ApiResponseData

object GetSubjectResponse {
  implicit val writes: OWrites[GetSubjectResponse] = Json.writes[GetSubjectResponse]

  final case class Subject(
    subjectId: UUID,
    title: String,
    displayStyle: SubjectDisplayStyleValue,
    courses: Iterable[GetSubjectResponse.Subject.Course]
  )

  object Subject {
    implicit val writes: OWrites[Subject] = Json.writes[Subject]

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

