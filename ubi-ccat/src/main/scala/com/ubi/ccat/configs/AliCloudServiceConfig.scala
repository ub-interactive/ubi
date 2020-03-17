package com.ubi.ccat.configs

import com.typesafe.config.Config
import play.api.{ConfigLoader, Configuration}

case class AliCloudServiceConfig(
  regionId: String,
  accessKey: String,
  accessSecret: String,
  signName: String
)

object AliCloudServiceConfig {
  implicit val configLoader: ConfigLoader[AliCloudServiceConfig] =
    (config: Config, path: String) => {
      val c = Configuration(config.getConfig(path))
      AliCloudServiceConfig(
        regionId = c.get[String]("region-id"),
        accessKey = c.get[String]("access-key"),
        accessSecret = c.get[String]("access-secret"),
        signName = c.get[String]("sign-name")
      )
    }
}
