/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl.api.commands

import com.stey.connect.lifesmart.impl.api.{LifesmartCommandRequest, LifesmartCommandResponse, ResponseCodeValue}
import play.api.libs.json._

/** @author liangliao at 2018/9/4 5:55 PM
 */
final case class EpAdd(
                        agt: String
                      )
  extends LifesmartCommandRequest {
  type RESPONSE = EpAddResponse

  override val method: String = "EpAdd"

  override def params: Map[String, String] = {
    Map(
      "agt" -> agt
      )
  }
}

object EpAdd {
  implicit val format: OFormat[EpAdd] = Json.format[EpAdd]
}

final case class EpAddResponse(
                                override val id: Long,
                                override val code: ResponseCodeValue,
                                override val message: EpAddResponse.Message
                              ) extends LifesmartCommandResponse[EpAddResponse.Message]

object EpAddResponse {
  implicit val format: OFormat[EpAddResponse] = Json.format[EpAddResponse]

  final case class Message(me: String)

  object Message {
    implicit val format: OFormat[Message] = Json.format[Message]
  }

}
