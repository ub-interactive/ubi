package com.ubi.crm.impl.user

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.ubi.crm.impl.user.UserAggregate.UserCreated

import scala.collection.immutable.Vector

object UserEntitySerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Vector[JsonSerializer[_]] = {
    Vector(
      /* user aggregate */
      JsonSerializer[UserCreated],
      JsonSerializer[UserState]
    )
  }
}
