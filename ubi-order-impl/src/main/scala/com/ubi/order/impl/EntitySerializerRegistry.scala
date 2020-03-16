package com.ubi.order.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable.Vector

object EntitySerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Vector[JsonSerializer[_]] = {
    Vector(
      /* order aggregate */
//      JsonSerializer[ApartmentOrderLinked],
//      JsonSerializer[HotelOrderLinked],
//      JsonSerializer[OrderUnlinked],
//      JsonSerializer[ApiInvocationLogged],
//      JsonSerializer[ReservationState],
//      JsonSerializer[ApartmentReservationState],
//      JsonSerializer[HotelReservationState]
    )
  }
}
