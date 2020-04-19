/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl.api.commands

import com.stey.connect.lifesmart.impl.api.{LifesmartCommandRequest, LifesmartCommandResponse, ResponseCodeValue}
import play.api.libs.json
import play.api.libs.json._

/** @author liangliao at 2018/9/5 2:55 PM
 */
case object WbAuth extends LifesmartCommandRequest {

  type RESPONSE = WbAuthResponse

  override val method: String = "WbAuth"

  implicit val format: Format[WbAuth.type] =
    new json.Format[WbAuth.type] {
      override def reads(json: JsValue): JsResult[WbAuth.type] = {
        JsSuccess(WbAuth)
      }

      override def writes(o: WbAuth.this.type): JsValue = {
        JsNull
      }
    }
}

final case class WbAuthResponse(
                                 override val id: Long,
                                 override val code: ResponseCodeValue,
                                 override val message: String
                               ) extends LifesmartCommandResponse[String]

object WbAuthResponse {
  implicit val reads: OFormat[WbAuthResponse] = Json.format[WbAuthResponse]
}
