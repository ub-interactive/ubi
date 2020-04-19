package com.ubi.qdog.controllers.api.console.course

import java.time.Instant
import java.util.UUID

import com.ubi.qdog.controllers.api.ApiResponseData
import com.ubi.qdog.controllers.api.console.course.ListCourseResponse.Course
import com.ubi.qdog.enums.CourseSaleTypeValue
import com.ubi.qdog.entities.CourseEntity.Tags
import play.api.libs.json.{Json, OFormat}

final case class ListCourseResponse(
  courses: Seq[Course]
) extends ApiResponseData

object ListCourseResponse {
  implicit val format: OFormat[ListCourseResponse] = Json.format[ListCourseResponse]

  final case class Course(
    courseId: UUID,
    title: String,
    subtitle: Option[String],
    thumbnailUrl: Option[String],
    coverUrl: String,
    price: Int,
    promotionPrice: Option[Int],
    saleType: CourseSaleTypeValue,
    tags: Tags,
    courseIntro: Option[String],
    courseMenu: Option[String],
    courseInfo: Option[String],
    flashSaleStartAt: Option[Instant],
    flashSaleEndAt: Option[Instant],
    saleStock: Option[Int]
  )

  object Course {
    implicit val format: OFormat[Course] = Json.format[Course]
  }

}

