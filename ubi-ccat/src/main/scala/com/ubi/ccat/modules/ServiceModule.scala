package com.ubi.ccat.modules

import com.google.inject
import com.ubi.ccat.providers.CrmServiceProvider
import com.ubi.crm.api.CrmService
import play.api.inject.Binding
import play.api.{Configuration, Environment}

class ServiceModule extends play.api.inject.Module {

  override def bindings(
    environment: Environment,
    configuration: Configuration
  ): Seq[Binding[_]] = {
    Seq(
      bind[CrmService].toProvider[CrmServiceProvider].in(classOf[inject.Singleton]),
    )
  }
}