package com.ubi.qdog.providers

import com.google.inject.{Inject, Provider}
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl
import play.api.Configuration

class WxMpServiceProvider @Inject()(configuration: Configuration) extends Provider[WxMpService] {
  private val wxConfig: Configuration = configuration.get[Configuration]("wechat.mp")

  override def get(): WxMpService = {
    val wxMpServiceImpl = new WxMpServiceImpl()
    wxMpServiceImpl.setWxMpConfigStorage(new WxMpDefaultConfigImpl {
      appId = wxConfig.get[String]("app-id")
      secret = wxConfig.get[String]("app-secret")
      token = wxConfig.get[String]("token")
      aesKey = wxConfig.get[String]("aes-key")
    })
    wxMpServiceImpl
  }
}
