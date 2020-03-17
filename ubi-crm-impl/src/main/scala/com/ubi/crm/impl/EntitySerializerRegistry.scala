package com.ubi.crm.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.ubi.crm.impl.user.UserAggregate.UserCreated
import com.ubi.crm.impl.user.UserState

import scala.collection.immutable.Vector

object EntitySerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Vector[JsonSerializer[_]] = {
    Vector(
      /* user aggregate */
      JsonSerializer[UserCreated],
      JsonSerializer[UserState]
    )
  }
}
