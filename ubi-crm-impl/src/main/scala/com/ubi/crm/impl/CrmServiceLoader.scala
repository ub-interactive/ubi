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
import com.ubi.crm.api.CrmService
import com.ubi.crm.impl.user.{CrmAggregate, CrmEntitySerializerRegistry, CrmServiceImpl}
import play.api.db.HikariCPComponents
import play.api.libs.ws.ahc.AhcWSComponents

class CrmServiceLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication = {
    new CrmServiceApplication(context) with ConfigurationServiceLocatorComponents
  }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication = {
    new CrmServiceApplication(context) with LagomDevModeComponents
  }

  override def describeService: Some[Descriptor] = {
    Some(readDescriptor[CrmService])
  }
}

abstract class CrmServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with SlickPersistenceComponents
    with HikariCPComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = CrmEntitySerializerRegistry

  override lazy val lagomServer: LagomServer = serverFor[CrmService](wire[CrmServiceImpl])

  clusterSharding.init(Entity(CrmAggregate.typeKey) { entityContext => CrmAggregate(entityContext) })

  //  readSide.register(wire[CrmEventProcessor])
}
