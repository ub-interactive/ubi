/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl.api.commands

import com.stey.connect.lifesmart.impl.api.{LifesmartCommandRequest, LifesmartCommandResponse, ResponseCodeValue}
import play.api.libs.json
import play.api.libs.json._

/** @author liangliao at 2018/9/5 2:55 PM
 */
case object RmAuth extends LifesmartCommandRequest {
  type RESPONSE = RmAuthResponse

  override val method: String = "RmAuth"

  implicit val format: Format[RmAuth.type] =
    new json.Format[RmAuth.type] {
      override def reads(json: JsValue): JsResult[RmAuth.type] = {
        JsSuccess(RmAuth)
      }

      override def writes(o: RmAuth.this.type): JsValue = {
        JsNull
      }
    }
}

final case class RmAuthResponse(
                                 override val id: Long,
                                 override val code: ResponseCodeValue,
                                 override val message: String
                               ) extends LifesmartCommandResponse[String]

object RmAuthResponse {
  implicit val format: OFormat[RmAuthResponse] = Json.format[RmAuthResponse]
}
