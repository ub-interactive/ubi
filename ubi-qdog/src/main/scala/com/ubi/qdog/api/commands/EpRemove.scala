/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl.api.commands

import com.stey.connect.lifesmart.impl.api.{LifesmartCommandRequest, LifesmartCommandResponse, ResponseCodeValue}
import play.api.libs.json._

/** @author liangliao at 2018/9/4 5:55 PM
 */
final case class EpRemove(
                           agt: String,
                           me: String
                         )
  extends LifesmartCommandRequest {
  type RESPONSE = EpRemoveResponse

  override val method: String = "EpRemove"

  override def params: Map[String, String] = {
    Map(
      "agt" -> agt,
      "me" -> me
      )
  }
}

object EpRemove {
  implicit val format: OFormat[EpRemove] = Json.format[EpRemove]
}

final case class EpRemoveResponse(
                                   override val id: Long,
                                   override val code: ResponseCodeValue,
                                   override val message: String
                                 ) extends LifesmartCommandResponse[String]

object EpRemoveResponse {

  implicit val format: OFormat[EpRemoveResponse] = Json.format[EpRemoveResponse]

}
