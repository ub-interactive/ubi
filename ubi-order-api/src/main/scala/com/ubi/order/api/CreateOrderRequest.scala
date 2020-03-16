package com.ubi.order.api

import java.util.UUID

import play.api.libs.json.{Json, OFormat}

final case class CreateOrderRequest(
  courseId: UUID,
  price: Int,
  quantity: Int
)

object CreateOrderRequest {
  implicit val format: OFormat[CreateOrderRequest] = Json.format[CreateOrderRequest]
}
