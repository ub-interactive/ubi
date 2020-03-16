package com.ubi.order.impl

import akka.cluster.sharding.typed.scaladsl.Entity
import com.lightbend.lagom.scaladsl.api.Descriptor
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.client.ConfigurationServiceLocatorComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.slick.SlickPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import com.ubi.order.api.OrderService
import play.api.db.HikariCPComponents
import play.api.libs.ws.ahc.AhcWSComponents
import redis.RedisClient

class OrderServiceLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication = {
    new OrderServiceApplication(context) with ConfigurationServiceLocatorComponents
  }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication = {
    new OrderServiceApplication(context) with LagomDevModeComponents
  }

  override def describeService: Some[Descriptor] = {
    Some(readDescriptor[OrderService])
  }
}

abstract class OrderServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with SlickPersistenceComponents
    with HikariCPComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = EntitySerializerRegistry

  override lazy val lagomServer: LagomServer = serverFor[OrderService](wire[OrderServiceImpl])

  clusterSharding.init(Entity(ReservationAggregate.typeKey) { entityContext => ReservationAggregate(entityContext) })

//  readSide.register(wire[ReservationEventProcessor])
}
