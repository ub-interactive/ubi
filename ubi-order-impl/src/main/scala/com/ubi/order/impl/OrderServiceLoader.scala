package com.ubi.order.impl

import akka.cluster.sharding.typed.scaladsl.Entity
import com.lightbend.lagom.scaladsl.api.Descriptor
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.slick.SlickPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import play.api.db.HikariCPComponents
import play.api.libs.ws.ahc.AhcWSComponents
import redis.RedisClient

class OrderServiceLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication = {
    new QtelsApplication(context) with AkkaDiscoveryComponents
  }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication = {
    new QtelsApplication(context) with LagomDevModeComponents
  }

  override def describeService: Some[Descriptor] = {
    Some(readDescriptor[QtelsService])
  }
}

abstract class QtelsApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with SlickPersistenceComponents
    with HikariCPComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  lazy val qtelsConfig: QtelsConfig = QtelsConfig.configLoader.load(config, "stey.connect.qtels")
  lazy val steyRedisConfig: RedisConfig = RedisConfig.configLoader.load(config, "redis.instance")
  lazy val redis: RedisClient = RedisClient(host = steyRedisConfig.host, password = Some(steyRedisConfig.password), port = steyRedisConfig.port, db = Some(steyRedisConfig.database))(_system = actorSystem)

  lazy val wsService: WsService = wire[WsService]
  lazy val qtelsExecutor: QtelsExecutor = wire[QtelsExecutor]

  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = EntitySerializerRegistry

  override lazy val lagomServer: LagomServer = serverFor[QtelsService](wire[QtelsServiceImpl])

  clusterSharding.init(Entity(ReservationAggregate.typeKey) { entityContext => ReservationAggregate(entityContext) })

  readSide.register(wire[ReservationEventProcessor])
}
