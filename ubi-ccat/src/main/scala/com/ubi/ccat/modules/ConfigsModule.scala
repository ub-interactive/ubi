package com.ubi.ccat.modules

import com.ubi.ccat.configs.AliCloudServiceConfig
import play.api.inject.Binding
import play.api.{Configuration, Environment}

class ConfigsModule extends play.api.inject.Module {

  override def bindings(
    environment: Environment,
    configuration: Configuration
  ): Seq[Binding[_]] = {
    Seq(
      bind[AliCloudServiceConfig].toInstance(configuration.get[AliCloudServiceConfig]("ali-cloud-service"))
    )
  }
}