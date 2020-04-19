/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl.api.commands

import com.stey.connect.lifesmart.impl.api.{LifesmartCommandRequest, LifesmartCommandResponse, ResponseCodeValue}
import play.api.libs.json.{Json, OFormat}

/** @author liangliao at 2018/9/4 5:55 PM
 */
final case class EpSet(
                        agt: String,
                        me: Option[String],
                        name: Option[String],
                        idx: Option[String],
                        `type`: Option[Int],
                        `val`: Option[Long]
                      ) extends LifesmartCommandRequest {
  type RESPONSE = EpSetResponse

  override val method: String = "EpSet"

  override def params: Map[String, String] = {
    Map(
      "agt" -> Some(agt),
      "me" -> Some(me.getOrElse("NULL")),
      "name" -> name,
      "idx" -> idx,
      "type" -> `type`.map(_.toString),
      "val" -> `val`.map(_.toString)
      ).collect { case (k, Some(v)) => k -> v }
  }
}

object EpSet {
  implicit val format: OFormat[EpSet] = Json.format[EpSet]
}

final case class EpSetResponse(
                                override val id: Long,
                                override val code: ResponseCodeValue,
                                override val message: String
                              ) extends LifesmartCommandResponse[String]

object EpSetResponse {
  implicit val format: OFormat[EpSetResponse] = Json.format[EpSetResponse]
}
