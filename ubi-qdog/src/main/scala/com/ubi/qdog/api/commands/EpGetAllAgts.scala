/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl.api.commands

import com.stey.connect.lifesmart.impl.api.{LifesmartCommandRequest, LifesmartCommandResponse, ResponseCodeValue}
import com.stey.connect.lifesmart.impl.models.Agt
import play.api.libs.json
import play.api.libs.json._

/** @author liangliao at 2018/9/4 5:55 PM
 */
case object EpGetAllAgts extends LifesmartCommandRequest {
  type RESPONSE = EpGetAllAgtsResponse

  override val method: String = "EpGetAllAgts"

  implicit val format: Format[EpGetAllAgts.type] = new json.Format[EpGetAllAgts.type] {
    override def reads(json: JsValue): JsResult[EpGetAllAgts.type] = {
      JsSuccess(EpGetAllAgts)
    }

    override def writes(o: EpGetAllAgts.this.type): JsValue = {
      JsNull
    }
  }
}

final case class EpGetAllAgtsResponse(
                                       override val id: Long,
                                       override val code: ResponseCodeValue,
                                       override val message: Seq[Agt]
                                     ) extends LifesmartCommandResponse[Seq[Agt]]

object EpGetAllAgtsResponse {
  implicit val format: OFormat[EpGetAllAgtsResponse] = Json.format[EpGetAllAgtsResponse]
}
