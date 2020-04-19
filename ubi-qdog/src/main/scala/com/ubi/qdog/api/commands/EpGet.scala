/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl.api.commands

import com.stey.connect.lifesmart.impl.api.{LifesmartCommandRequest, LifesmartCommandResponse, ResponseCodeValue}
import com.stey.connect.lifesmart.impl.models.Device
import play.api.libs.json._

/** @author liangliao at 2018/9/4 5:55 PM
 */
final case class EpGet(
                        agt: String,
                        me: String
                      )
  extends LifesmartCommandRequest {
  type RESPONSE = EpGetResponse

  override val method: String = "EpGet"

  override def params: Map[String, String] = {
    Map(
      "agt" -> agt,
      "me" -> me
      )
  }
}

object EpGet {
  implicit val format: OFormat[EpGet] = Json.format[EpGet]
}

final case class EpGetResponse(
                                override val id: Long,
                                override val code: ResponseCodeValue,
                                override val message: Device
                              ) extends LifesmartCommandResponse[Device]

object EpGetResponse {
  implicit val format: OFormat[EpGetResponse] = Json.format[EpGetResponse]
}
