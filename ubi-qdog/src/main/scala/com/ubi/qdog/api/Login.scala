/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl.api

import com.stey.common.ws._
import play.api.libs.json.{Json, OFormat}

/**
 * @author liangliao at 2018/9/3 8:41 PM
 */
final case class Login(
                        uid: String,
                        pwd: String,
                        appkey: String,
                        did: Option[String]
                      )
  extends LifesmartRequest {
  type RESPONSE = LoginResponse
  override val httpMethod: HttpMethodValue = HttpMethod.POST
  override val path: String = "/auth.login"
}

object Login {
  implicit val format: OFormat[Login] = Json.format[Login]
  implicit val builder: LifesmartRequestBuilder[Login] = new LifesmartRequestBuilder[Login]
  implicit val parser: JsValueResponseParser[Login] = new JsValueResponseParser[Login]
}

final case class LoginResponse(
                                rgn: String,
                                code: String,
                                userid: String,
                                token: String,
                                rgnid: String
                              )
  extends LifesmartResponse

object LoginResponse {
  implicit val format: OFormat[LoginResponse] = Json.format[LoginResponse]
}
