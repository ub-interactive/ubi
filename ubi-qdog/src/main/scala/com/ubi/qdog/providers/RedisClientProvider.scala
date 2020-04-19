package com.ubi.qdog.providers

import akka.actor.ActorSystem
import com.google.inject.{Inject, Provider}
import com.typesafe.config.Config
import play.api.{ConfigLoader, Configuration}
import redis.RedisClient

final case class RedisConfig(
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

class RedisClientProvider @Inject()(configuration: Configuration)
  (implicit val actorSystem: ActorSystem) extends Provider[RedisClient] {

  private val redisConfig = configuration.get[RedisConfig]("redis.instance")

  override def get(): RedisClient = {
    RedisClient(host = redisConfig.host, password = Some(redisConfig.password), port = redisConfig.port, db = Some(redisConfig.database))
  }
}
