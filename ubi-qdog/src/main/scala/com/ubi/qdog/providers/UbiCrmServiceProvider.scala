package com.ubi.qdog.providers

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.{Inject, Provider}
import com.lightbend.lagom.scaladsl.client.{CircuitBreakerComponents, ConfigurationServiceLocatorComponents, LagomClientFactory}
import com.ubi.crm.api.CrmService
import play.api.Environment
import play.api.inject.ApplicationLifecycle
import play.api.libs.ws.WSClient

abstract class UbiCrmServiceFactory extends LagomClientFactory("ubi-crm-service", classOf[UbiCrmServiceFactory].getClassLoader)

class CrmServiceProvider @Inject()(
  client: WSClient,
  mat: Materializer,
  system: ActorSystem,
  env: Environment,
  lifecycle: ApplicationLifecycle
) extends Provider[CrmService] {
  override def get(): CrmService = {

    val factory: UbiCrmServiceFactory with CircuitBreakerComponents = new UbiCrmServiceFactory with ConfigurationServiceLocatorComponents {
      override val wsClient: WSClient = client
      override val materializer: Materializer = mat
      override val actorSystem: ActorSystem = system
      override lazy val environment: Environment = env
      override lazy val applicationLifecycle: ApplicationLifecycle = lifecycle
    }
    factory.serviceClient.implement[CrmService]
  }
}