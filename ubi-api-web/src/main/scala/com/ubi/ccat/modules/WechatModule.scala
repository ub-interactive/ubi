//package com.ubi.modules
//
//import cn.binarywang.wx.miniapp.api.WxMaService
//import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl
//import com.github.binarywang.wxpay.config.WxPayConfig
//import com.github.binarywang.wxpay.service.WxPayService
//import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl
//import com.google.inject.{AbstractModule, Provides}
//import javax.inject.Singleton
//import me.chanjar.weixin.mp.api.WxMpService
//import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl
//import me.chanjar.weixin.open.api.WxOpenService
//import me.chanjar.weixin.open.api.impl.{WxOpenInMemoryConfigStorage, WxOpenServiceImpl}
//import play.api.Configuration
//
///**
// * Created by liangliao on 25/7/17.
// */
//class WechatModule extends AbstractModule {
//
//  override def configure(): Unit = {}
//
//  @Provides
//  @Singleton
//  def provideWechatMpService(configuration: Configuration): WxMpService = {
//    getWechatMpServiceImpl(configuration.get[Configuration]("wechat.mp"))
//  }
//
//  @Provides
//  @Singleton
//  def provideWechatPayService(configuration: Configuration): WxPayService = {
//    getWechatPayServiceImpl(configuration.get[Configuration]("wechat.pay"))
//  }
//
//  @Provides
//  @Singleton
//  def provideWechatOpenService(configuration: Configuration): WxOpenService = {
//    getWechatOpenServiceImpl(configuration.get[Configuration]("wechat.open"))
//  }
//
//  @Provides
//  @Singleton
//  def provideWechatMiniappService(configuration: Configuration): WxMaService = {
//    getWechatMiniappServiceImp(configuration.get[Configuration]("wechat.miniapp"))
//  }
//
//  private def getWechatMpServiceImpl(wxConfig: Configuration): WxMpService = {
//
//    val wxMpServiceImpl = new WxMpServiceImpl()
//    wxMpServiceImpl.setWxMpConfigStorage(new WxMpInMemoryConfigStorage {
//      appId = wxConfig.get[String]("appId")
//      secret = wxConfig.get[String]("appSecret")
//      token = wxConfig.get[String]("token")
//      aesKey = wxConfig.get[String]("aesKey")
//    })
//    wxMpServiceImpl
//  }
//
//  private def getWechatPayServiceImpl(wxConfig: Configuration): WxPayService = {
//    val wxPayConfig = new WxPayConfig()
//    wxPayConfig.setAppId(wxConfig.get[String]("appId"))
//    wxPayConfig.setMchId(wxConfig.get[String]("mcId"))
//    wxPayConfig.setMchKey(wxConfig.get[String]("mcKey"))
//    val wxPayServiceImpl = new WxPayServiceImpl
//    wxPayServiceImpl.setConfig(wxPayConfig)
//    wxPayServiceImpl
//  }
//
//  private def getWechatOpenServiceImpl(wxConfig: Configuration): WxOpenService = {
//    val wxOpenInMemoryConfigStorage = new WxOpenInMemoryConfigStorage
//    wxOpenInMemoryConfigStorage.setComponentAppId(wxConfig.get[String]("componentAppId"))
//    wxOpenInMemoryConfigStorage.setComponentAppSecret(wxConfig.get[String]("componentSecret"))
//    wxOpenInMemoryConfigStorage.setComponentToken(wxConfig.get[String]("componentToken"))
//    wxOpenInMemoryConfigStorage.setComponentAesKey(wxConfig.get[String]("componentAesKey"))
//    val wxOpenServiceImpl = new WxOpenServiceImpl
//    wxOpenServiceImpl.setWxOpenConfigStorage(wxOpenInMemoryConfigStorage)
//    wxOpenServiceImpl
//  }
//
//  private def getWechatMiniappServiceImp(wxConfig: Configuration): WxMaService = {
//    val wxMaInMemoryConfig = new WxMaInMemoryConfig
//    wxMaInMemoryConfig.setAppid(wxConfig.get[String]("appId"))
//    wxMaInMemoryConfig.setSecret(wxConfig.get[String]("appSecret"))
//    //    wxMaInMemoryConfig.setToken(wxConfig.get[String]("token"))
//    //    wxMaInMemoryConfig.setAesKey(wxConfig.get[String]("aesKey"))
//    val wxMaService = new WxMaServiceImpl
//    wxMaService.setWxMaConfig(wxMaInMemoryConfig)
//    wxMaService
//  }
//
//
//}
