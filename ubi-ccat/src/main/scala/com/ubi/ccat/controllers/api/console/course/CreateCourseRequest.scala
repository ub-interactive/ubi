package com.ubi.ccat.controllers.api.console.course

import java.time.Instant

import com.ubi.ccat.controllers.api.ApiRequestData
import com.ubi.ccat.enums.CourseSaleTypeValue
import com.ubi.ccat.entities.CourseEntity.Tags
import play.api.libs.json.{Json, OFormat}

final case class CreateCourseRequest(
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

object CreateCourseRequest {
  implicit val format: OFormat[CreateCourseRequest] = Json.format[CreateCourseRequest]
}



