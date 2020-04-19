/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl.api.commands

import com.stey.connect.lifesmart.impl.api.{LifesmartCommandRequest, LifesmartCommandResponse, ResponseCodeValue}
import play.api.libs.json._

/** @author liangliao at 2018/9/4 5:55 PM
 */
final case class EpDeleteAgt(
                              agt: String
                            )
  extends LifesmartCommandRequest {
  type RESPONSE = EpDeleteAgtResponse

  override val method: String = "EpDeleteAgt"

  override def params: Map[String, String] = {
    Map(
      "agt" -> agt
      )
  }
}

object EpDeleteAgt {
  implicit val format: OFormat[EpDeleteAgt] = Json.format[EpDeleteAgt]
}

final case class EpDeleteAgtResponse(
                                      override val id: Long,
                                      override val code: ResponseCodeValue,
                                      override val message: String
                                    ) extends LifesmartCommandResponse[String]

object EpDeleteAgtResponse {
  implicit val format: OFormat[EpDeleteAgtResponse] = Json.format[EpDeleteAgtResponse]
}
