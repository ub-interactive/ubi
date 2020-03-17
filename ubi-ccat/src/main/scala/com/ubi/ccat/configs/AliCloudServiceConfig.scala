package com.ubi.ccat.configs

import com.typesafe.config.Config
import play.api.{ConfigLoader, Configuration}

case class AliCloudServiceConfig(
  accessKey: String,
  accessSecret: String
)

object AliCloudServiceConfig {
  implicit val configLoader: ConfigLoader[AliCloudServiceConfig] =
    (config: Config, path: String) ⇒ {
      val c = Configuration(config.getConfig(path))
      AliCloudServiceConfig(
        accessKey = c.get[String]("access-key"),
        accessSecret = c.get[String]("access-secret")
      )
    }
}
