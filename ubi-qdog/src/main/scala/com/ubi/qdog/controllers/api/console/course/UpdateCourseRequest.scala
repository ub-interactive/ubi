package com.ubi.qdog.controllers.api.console.course

import java.time.Instant

import com.ubi.qdog.controllers.api.ApiRequestData
import com.ubi.qdog.enums.CourseSaleTypeValue
import com.ubi.qdog.entities.CourseEntity.Tags
import play.api.libs.json.{Json, OFormat}

final case class UpdateCourseRequest(
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
) extends ApiRequestData

object UpdateCourseRequest {
  implicit val format: OFormat[UpdateCourseRequest] = Json.format[UpdateCourseRequest]
}




