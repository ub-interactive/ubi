package com.ubi.ccat.modules

import cn.binarywang.wx.miniapp.api.WxMaService
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl
import com.github.binarywang.wxpay.config.WxPayConfig
import com.github.binarywang.wxpay.service.WxPayService
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl
import com.google.inject.{AbstractModule, Provides}
import javax.inject.Singleton
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl
import me.chanjar.weixin.open.api.WxOpenService
import me.chanjar.weixin.open.api.impl.{WxOpenInMemoryConfigStorage, WxOpenServiceImpl}
import play.api.Configuration

/**
 * Created by liangliao on 25/7/17.
 */
class WechatModule extends AbstractModule {

  override def configure(): Unit = {}

  @Provides
  @Singleton
  def provideWechatMpService(configuration: Configuration): WxMpService = {
    getWechatMpServiceImpl(configuration.get[Configuration]("wechat.mp"))
  }

  @Provides
  @Singleton
  def provideWechatPayService(configuration: Configuration): WxPayService = {
    getWechatPayServiceImpl(configuration.get[Configuration]("wechat.pay"))
  }

  @Provides
  @Singleton
  def provideWechatOpenService(configuration: Configuration): WxOpenService = {
    getWechatOpenServiceImpl(configuration.get[Configuration]("wechat.open"))
  }

  @Provides
  @Singleton
  def provideWechatMiniappService(configuration: Configuration): WxMaService = {
    getWechatMiniappServiceImp(configuration.get[Configuration]("wechat.miniapp"))
  }

  private def getWechatMpServiceImpl(wxConfig: Configuration): WxMpService = {
    //    WxMpInMemoryConfigStorage
    val wxMpServiceImpl = new WxMpServiceImpl()
    wxMpServiceImpl.setWxMpConfigStorage(new WxMpDefaultConfigImpl {
      appId = wxConfig.get[String]("app-id")
      secret = wxConfig.get[String]("app-secret")
      token = wxConfig.get[String]("token")
      aesKey = wxConfig.get[String]("aes-key")
    })
    wxMpServiceImpl
  }

  private def getWechatPayServiceImpl(wxConfig: Configuration): WxPayService = {
    val wxPayConfig = new WxPayConfig()
    wxPayConfig.setAppId(wxConfig.get[String]("app-id"))
    wxPayConfig.setMchId(wxConfig.get[String]("mc-id"))
    wxPayConfig.setMchKey(wxConfig.get[String]("mc-key"))
    val wxPayServiceImpl = new WxPayServiceImpl
    wxPayServiceImpl.setConfig(wxPayConfig)
    wxPayServiceImpl
  }

  private def getWechatOpenServiceImpl(wxConfig: Configuration): WxOpenService = {
    val wxOpenInMemoryConfigStorage = new WxOpenInMemoryConfigStorage
    wxOpenInMemoryConfigStorage.setComponentAppId(wxConfig.get[String]("component-app-id"))
    wxOpenInMemoryConfigStorage.setComponentAppSecret(wxConfig.get[String]("component-secret"))
    wxOpenInMemoryConfigStorage.setComponentToken(wxConfig.get[String]("component-token"))
    wxOpenInMemoryConfigStorage.setComponentAesKey(wxConfig.get[String]("component-aes-key"))
    val wxOpenServiceImpl = new WxOpenServiceImpl
    wxOpenServiceImpl.setWxOpenConfigStorage(wxOpenInMemoryConfigStorage)
    wxOpenServiceImpl
  }

  private def getWechatMiniappServiceImp(wxConfig: Configuration): WxMaService = {
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
