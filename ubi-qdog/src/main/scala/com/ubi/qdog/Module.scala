package com.ubi.qdog

import cn.binarywang.wx.miniapp.api.WxMaService
import com.github.binarywang.wxpay.service.WxPayService
import com.google.inject
import com.ubi.qdog.providers._
import com.ubi.crm.api.CrmService
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.open.api.WxOpenService
import play.api.inject.Binding
import play.api.{Configuration, Environment}
import redis.RedisClient

class Module extends play.api.inject.Module {
  override def bindings(
    environment: Environment,
    configuration: Configuration
  ): collection.Seq[Binding[_]] = {
    Seq(
      bind[CrmService].toProvider[CrmServiceProvider].in(classOf[inject.Singleton]),
      bind[RedisClient].toProvider[RedisClientProvider].in(classOf[inject.Singleton]),
      bind[ShortMessageService].toProvider[ShortMessageServiceProvider].in(classOf[inject.Singleton]),
      bind[WxMaService].toProvider[WxMaServiceProvider].in(classOf[inject.Singleton]),
      bind[WxMpService].toProvider[WxMpServiceProvider].in(classOf[inject.Singleton]),
      bind[WxOpenService].toProvider[WxOpenServiceProvider].in(classOf[inject.Singleton]),
      bind[WxPayService].toProvider[WxPayServiceProvider].in(classOf[inject.Singleton])
    )
  }
}
