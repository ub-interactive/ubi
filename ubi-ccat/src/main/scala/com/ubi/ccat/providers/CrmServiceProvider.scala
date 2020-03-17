package com.ubi.ccat.providers

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.{Inject, Provider}
import com.lightbend.lagom.scaladsl.client.{CircuitBreakerComponents, ConfigurationServiceLocatorComponents, LagomClientFactory}
import com.ubi.crm.api.CrmService
import play.api.Environment
import play.api.inject.ApplicationLifecycle
import play.api.libs.ws.WSClient

abstract class CrmServiceFactory extends LagomClientFactory("crm-service", classOf[CrmServiceFactory].getClassLoader)

class CrmServiceProvider @Inject()(
  client: WSClient,
  mat: Materializer,
  system: ActorSystem,
  env: Environment,
  lifecycle: ApplicationLifecycle
) extends Provider[CrmService] {
  override def get(): CrmService = {

    val factory: CrmServiceFactory with CircuitBreakerComponents = new CrmServiceFactory with ConfigurationServiceLocatorComponents {
      override val wsClient: WSClient = client
      override val materializer: Materializer = mat
      override val actorSystem: ActorSystem = system
      override lazy val environment: Environment = env
      override lazy val applicationLifecycle: ApplicationLifecycle = lifecycle
    }
    factory.serviceClient.implement[CrmService]
  }
}