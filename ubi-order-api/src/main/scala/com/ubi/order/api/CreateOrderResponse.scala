package com.ubi.order.api

import java.util.UUID

import com.ubi.order.api.enums.OrderStatusValue

final case class CreateOrderResponse(
  orderId: UUID,
  courseId: UUID,
  orderStatus: OrderStatusValue,
  price: Int,
  quantity: Int,
  totalAmount: Int
)
