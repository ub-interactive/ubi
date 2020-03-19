package com.ubi.ccat.providers

import com.github.binarywang.wxpay.config.WxPayConfig
import com.github.binarywang.wxpay.service.WxPayService
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl
import com.google.inject.{Inject, Provider}
import play.api.Configuration

class WxPayServiceProvider @Inject()(configuration: Configuration) extends Provider[WxPayService] {
  private val wxConfig: Configuration = configuration.get[Configuration]("wechat.pay")

  override def get(): WxPayService = {
    val wxPayConfig = new WxPayConfig()
    wxPayConfig.setAppId(wxConfig.get[String]("app-id"))
    wxPayConfig.setMchId(wxConfig.get[String]("mc-id"))
    wxPayConfig.setMchKey(wxConfig.get[String]("mc-key"))
    val wxPayServiceImpl = new WxPayServiceImpl
    wxPayServiceImpl.setConfig(wxPayConfig)
    wxPayServiceImpl
  }
}
