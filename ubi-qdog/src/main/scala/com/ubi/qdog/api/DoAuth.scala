/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl.api

import akka.util.ByteString
import com.stey.common.ws._
import play.api.libs.json.{Json, OFormat}
import redis.{ByteStringDeserializer, ByteStringSerializer}

/**
 * @author liangliao at 2018/11/11 4:41 PM
 */
final case class DoAuth(
                         userid: String,
                         appkey: String,
                         token: String,
                         did: Option[String],
                         rgn: String
                       )
  extends LifesmartRequest {
  type RESPONSE = DoAuthResponse
  override val httpMethod: HttpMethodValue = HttpMethod.POST
  override val path: String = "/auth.do_auth"
}

object DoAuth {
  implicit val format: OFormat[DoAuth] = Json.format[DoAuth]
  implicit val builder: LifesmartRequestBuilder[DoAuth] = new LifesmartRequestBuilder[DoAuth]
  implicit val parser: JsValueResponseParser[DoAuth] = new JsValueResponseParser[DoAuth]
}

final case class DoAuthResponse(
                                 code: String,
                                 expiredtime: Long,
                                 svrurl: String,
                                 usertoken: String,
                                 userid: String
                               )
  extends LifesmartResponse

object DoAuthResponse {
  implicit val format: OFormat[DoAuthResponse] = Json.format[DoAuthResponse]

  implicit val serializer: ByteStringSerializer[DoAuthResponse] = (data: DoAuthResponse) => {
    ByteString(Json.toJson(data).toString)
  }

  implicit val deserializer: ByteStringDeserializer[DoAuthResponse] = (bs: ByteString) => {
    Json.parse(bs.utf8String).validate[DoAuthResponse].get
  }
}
