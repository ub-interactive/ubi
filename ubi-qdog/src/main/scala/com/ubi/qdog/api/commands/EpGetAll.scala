/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl.api.commands

import com.stey.connect.lifesmart.impl.api.{LifesmartCommandRequest, LifesmartCommandResponse, ResponseCodeValue}
import com.stey.connect.lifesmart.impl.models.Device
import play.api.libs.json
import play.api.libs.json._

/** @author liangliao at 2018/9/4 5:55 PM
 */
case object EpGetAll extends LifesmartCommandRequest {
  type RESPONSE = EpGetAllResponse

  override val method: String = "EpGetAll"

  implicit val format: Format[EpGetAll.type] = new json.Format[EpGetAll.type] {
    override def reads(json: JsValue): JsResult[EpGetAll.type] = {
      JsSuccess(EpGetAll)
    }

    override def writes(o: EpGetAll.this.type): JsValue = {
      JsNull
    }
  }
}

final case class EpGetAllResponse(
                                   override val id: Long,
                                   override val code: ResponseCodeValue,
                                   override val message: Seq[Device]
                                 ) extends LifesmartCommandResponse[Seq[Device]]

object EpGetAllResponse {
  implicit val format: OFormat[EpGetAllResponse] = Json.format[EpGetAllResponse]
}
