package com.ubi.ccat.controllers.api.web.course

import java.time.Instant
import java.util.UUID

import com.ubi.ccat.controllers.api.ApiResponseData
import com.ubi.ccat.enums.CourseSaleTypeValue
import com.ubi.ccat.persistence.slick.CourseEntity.Tags
import play.api.libs.json.{Json, OFormat}

final case class GetCourseResponse(
  courseId: UUID,
  title: String,
  subtitle: Option[String],
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
) extends ApiResponseData

object GetCourseResponse {
  implicit val format: OFormat[GetCourseResponse] = Json.format[GetCourseResponse]
}



