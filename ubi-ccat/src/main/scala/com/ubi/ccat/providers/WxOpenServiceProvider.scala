package com.ubi.ccat.providers

import com.google.inject.{Inject, Provider}
import me.chanjar.weixin.open.api.WxOpenService
import me.chanjar.weixin.open.api.impl.{WxOpenInMemoryConfigStorage, WxOpenServiceImpl}
import play.api.Configuration

class WxOpenServiceProvider @Inject()(configuration: Configuration) extends Provider[WxOpenService] {
  private val wxConfig: Configuration = configuration.get[Configuration]("wechat.open")

  override def get(): WxOpenService = {
    val wxOpenInMemoryConfigStorage = new WxOpenInMemoryConfigStorage
    wxOpenInMemoryConfigStorage.setComponentAppId(wxConfig.get[String]("component-app-id"))
    wxOpenInMemoryConfigStorage.setComponentAppSecret(wxConfig.get[String]("component-secret"))
    wxOpenInMemoryConfigStorage.setComponentToken(wxConfig.get[String]("component-token"))
    wxOpenInMemoryConfigStorage.setComponentAesKey(wxConfig.get[String]("component-aes-key"))
    val wxOpenServiceImpl = new WxOpenServiceImpl
    wxOpenServiceImpl.setWxOpenConfigStorage(wxOpenInMemoryConfigStorage)
    wxOpenServiceImpl
  }
}
