package com.ubi.ccat.ws

import play.api.libs.json.{Format, JsResult, JsString, JsValue}


/* HttpMethod */
final case class HttpMethodValue(value: String)

object HttpMethodValue {
  implicit val format: Format[HttpMethodValue] = {
    new Format[HttpMethodValue] {
      override def reads(json: JsValue): JsResult[HttpMethodValue] = {
        json.validate[String].map(HttpMethodValue.apply)
      }

      override def writes(o: HttpMethodValue): JsValue = {
        JsString(o.value)
      }
    }
  }
}

object HttpMethod {

  val OPTIONS: HttpMethodValue = HttpMethodValue("OPTIONS")

  val GET: HttpMethodValue = HttpMethodValue("GET")

  val HEAD: HttpMethodValue = HttpMethodValue("HEAD")

  val POST: HttpMethodValue = HttpMethodValue("POST")

  val PUT: HttpMethodValue = HttpMethodValue("PUT")

  val PATCH: HttpMethodValue = HttpMethodValue("PATCH")

  val DELETE: HttpMethodValue = HttpMethodValue("DELETE")

  val TRACE: HttpMethodValue = HttpMethodValue("TRACE")

  val CONNECT: HttpMethodValue = HttpMethodValue("CONNECT")

}
