package com.ubi.order.api

import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

class OrderService extends Service {

  def createOrder: ServiceCall[CreateOrderRequest, CreateOrderResponse]

  override def descriptor: Descriptor = {
    import Service._
    named("ubi-order-service").withCalls(
      pathCall("/order/create", createOrder _),
    )
  }
}
