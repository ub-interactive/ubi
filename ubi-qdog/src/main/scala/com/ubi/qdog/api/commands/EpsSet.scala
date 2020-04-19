/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl.api.commands

import com.stey.connect.lifesmart.impl.api.{LifesmartCommandRequest, LifesmartCommandResponse, ResponseCodeValue}
import play.api.libs.json._

/** @author liangliao at 2018/9/4 5:55 PM
 */
final case class EpsSet(args: Seq[EpSet]) extends LifesmartCommandRequest {
  type RESPONSE = EpsSetResponse

  override val method: String = "EpsSet"

  override def params: Map[String, String] = {
    Map(
      "args" -> Json.toJson(args).toString
      )
  }
}

object EpsSet {
  val writes: Writes[EpsSet] = Writes[EpsSet] { o =>
    JsObject(Map("args" -> JsString(Json.toJson(o.args).toString)))
  }

  val reads: Reads[EpsSet] = Reads[EpsSet] { jsValue =>
    (jsValue \ "args").validate[String].flatMap(Json.parse(_).validate[Seq[EpSet]]).map[EpsSet](EpsSet.apply)
  }

  implicit val format: Format[EpsSet] = Format(reads, writes)
}

final case class EpsSetResponse(
                                 override val id: Long,
                                 override val code: ResponseCodeValue,
                                 override val message: String
                               ) extends LifesmartCommandResponse[String]

object EpsSetResponse {
  implicit val format: OFormat[EpsSetResponse] = Json.format[EpsSetResponse]
}
