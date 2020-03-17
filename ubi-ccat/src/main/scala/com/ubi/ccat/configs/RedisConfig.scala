package com.ubi.ccat.configs

import com.typesafe.config.Config
import play.api.{ConfigLoader, Configuration}

case class RedisConfig(
  host: String,
  password: String,
  port: Int,
  database: Int
)

object RedisConfig {
  implicit val configLoader: ConfigLoader[RedisConfig] =
    (config: Config, path: String) => {
      val c = Configuration(config.getConfig(path))
      RedisConfig(
        host = c.get[String]("host"),
        password = c.get[String]("password"),
        port = c.get[Int]("port"),
        database = c.get[Int]("database")
      )
    }
}
