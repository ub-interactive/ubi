package com.ubi.order.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.ubi.order.api.{CreateOrderRequest, CreateOrderResponse, OrderService}

class OrderServiceImpl extends OrderService{
  override def createOrder: ServiceCall[CreateOrderRequest, CreateOrderResponse] = {
    ???
  }
}
