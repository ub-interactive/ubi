/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl.api.commands

import com.stey.connect.lifesmart.impl.api.{LifesmartCommandRequest, LifesmartCommandResponse, ResponseCodeValue}
import play.api.libs.json._

/** @author liangliao at 2018/9/4 5:55 PM
 */
final case class EpAddAgt(
                           sn: String,
                           name: String
                         )
  extends LifesmartCommandRequest {
  type RESPONSE = EpAddAgtResponse

  override val method: String = "EpAddAgt"

  override def params: Map[String, String] = {
    Map(
      "sn" -> sn,
      "name" -> name
      )
  }
}

object EpAddAgt {
  implicit val format: OFormat[EpAddAgt] = Json.format[EpAddAgt]
}

final case class EpAddAgtResponse(
                                   override val id: Long,
                                   override val code: ResponseCodeValue,
                                   override val message: EpAddAgtResponse.Message
                                 ) extends LifesmartCommandResponse[EpAddAgtResponse.Message]

object EpAddAgtResponse {
  implicit val format: OFormat[EpAddAgtResponse] = Json.format[EpAddAgtResponse]

  final case class Message(
                            agt: String,
                            name: String
                          )

  object Message {
    implicit val format: OFormat[Message] = Json.format[Message]
  }

}
