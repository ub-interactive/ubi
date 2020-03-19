package com.ubi.ccat.providers

import cn.binarywang.wx.miniapp.api.WxMaService
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl
import com.google.inject.{Inject, Provider}
import play.api.Configuration

class WxMaServiceProvider @Inject()(configuration: Configuration) extends Provider[WxMaService] {
  private val wxConfig: Configuration = configuration.get[Configuration]("wechat.miniapp")

  override def get(): WxMaService = {
    val wxMaInMemoryConfig = new WxMaDefaultConfigImpl
    wxMaInMemoryConfig.setAppid(wxConfig.get[String]("app-id"))
    wxMaInMemoryConfig.setSecret(wxConfig.get[String]("app-secret"))
    //    wxMaInMemoryConfig.setToken(wxConfig.get[String]("token"))
    //    wxMaInMemoryConfig.setAesKey(wxConfig.get[String]("aes-key"))
    val wxMaService = new WxMaServiceImpl
    wxMaService.setWxMaConfig(wxMaInMemoryConfig)
    wxMaService
  }
}
