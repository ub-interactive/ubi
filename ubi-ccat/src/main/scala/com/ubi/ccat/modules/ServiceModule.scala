package com.ubi.ccat.modules

import akka.actor.ActorSystem
import com.google.inject
import com.google.inject.{AbstractModule, Provides}
import com.ubi.ccat.configs.RedisConfig
import com.ubi.ccat.providers.CrmServiceProvider
import com.ubi.crm.api.CrmService
import play.api.Configuration
import redis.RedisClient

class ServiceModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[CrmService]).toProvider(classOf[CrmServiceProvider]).in(classOf[inject.Singleton])
  }

  @Provides
  def provideRedis(
    actorSystem: ActorSystem,
    configuration: Configuration
  ): RedisClient = {
    val redisConfig = configuration.get[RedisConfig]("redis.instances.default")
    RedisClient(host = redisConfig.host, password = Some(redisConfig.password), port = redisConfig.port, db = Some(redisConfig.database))(_system = actorSystem)
  }
}