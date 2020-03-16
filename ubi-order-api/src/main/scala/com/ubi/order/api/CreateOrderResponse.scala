package com.ubi.order.api

import java.util.UUID

import com.ubi.order.api.enums.OrderStatusValue
import play.api.libs.json.{Json, OFormat}

final case class CreateOrderResponse(
  orderId: UUID,
  courseId: UUID,
  orderStatus: OrderStatusValue,
  price: Int,
  quantity: Int,
  totalAmount: Int
)

object CreateOrderResponse {
  implicit val format: OFormat[CreateOrderResponse] = Json.format[CreateOrderResponse]
}
