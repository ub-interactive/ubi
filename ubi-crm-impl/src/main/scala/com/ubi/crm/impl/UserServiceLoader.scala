package com.ubi.crm.impl

import akka.cluster.sharding.typed.scaladsl.Entity
import com.lightbend.lagom.scaladsl.api.Descriptor
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.client.ConfigurationServiceLocatorComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.slick.SlickPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import com.ubi.crm.api.UserService
import play.api.db.HikariCPComponents
import play.api.libs.ws.ahc.AhcWSComponents

class UserServiceLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication = {
    new UserServiceApplication(context) with ConfigurationServiceLocatorComponents
  }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication = {
    new UserServiceApplication(context) with LagomDevModeComponents
  }

  override def describeService: Some[Descriptor] = {
    Some(readDescriptor[UserService])
  }
}

abstract class UserServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with SlickPersistenceComponents
    with HikariCPComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = EntitySerializerRegistry

  override lazy val lagomServer: LagomServer = serverFor[UserService](wire[UserServiceImpl])

  clusterSharding.init(Entity(UserAggregate.typeKey) { entityContext => UserAggregate(entityContext) })

  //  readSide.register(wire[UserEventProcessor])
}
