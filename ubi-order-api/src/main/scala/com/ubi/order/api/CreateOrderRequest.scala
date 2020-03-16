package com.ubi.order.api

import java.util.UUID

import play.api.libs.json.{Json, OWrites}

final case class CreateOrderRequest(
  courseId: UUID,
  price: Int,
  quantity: Int,
  totalAmount: Int
)

object CreateOrderRequest {
  implicit val writes: OWrites[CreateOrderRequest] = Json.writes[CreateOrderRequest]
}
