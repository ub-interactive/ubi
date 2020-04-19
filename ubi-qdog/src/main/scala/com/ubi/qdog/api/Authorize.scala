/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl.api

import java.security.MessageDigest

import com.stey.common.ws._
import play.api.libs.json.{Json, OFormat}

import scala.collection.immutable.ListMap

/** @author liangliao at 2018/11/11 4:41 PM
 */
final case class Authorize(
                            id: String,
                            did: Option[String],
                            lang: String,
                            appkey: String,
                            apptoken: String,
                            auth_callback: String,
                            time: Long,
                            sign: String
                          ) extends LifesmartRequest {
  type RESPONSE = AuthorizeResponse
  override val httpMethod: HttpMethodValue = HttpMethod.GET
  override val path: String = "/auth.authorize"

  override def queryStringParameters: ListMap[String, String] = {
    ListMap(
      "id" -> Some(id),
      "did" -> did,
      "lang" -> Some(lang),
      "appkey" -> Some(appkey),
      "apptoken" -> Some(apptoken),
      "auth_callback" -> Some(auth_callback),
      "time" -> Some(time.toString),
      "sign" -> Some(sign)
      ) collect { case (key, Some(value)) => key -> value }
  }
}

object Authorize {

  def apply(
             id: String,
             did: Option[String],
             lang: String,
             appkey: String,
             apptoken: String,
             auth_callback: String,
             time: Long
           ): Authorize = {
    val signature = sign(Seq(
      "appkey" -> Some(appkey),
      "auth_callback" -> Some(auth_callback),
      "did" -> did,
      "time" -> Some(time.toString),
      "apptoken" -> Some(apptoken)
      ).collect { case (key, Some(value)) => key -> value })
    new Authorize(id, did, lang, appkey, apptoken, auth_callback, time, sign = signature)
  }

  private def sign(params: Seq[(String, String)]): String = {
    val paramString = params.map { case (key, value) => s"$key=$value" }.mkString("&")
    MessageDigest.getInstance("MD5").digest(paramString.getBytes).map(0xFF & _).map("%02x".format(_)).foldLeft("")(_ + _)
  }

  implicit val format: OFormat[Authorize] = Json.format[Authorize]
}

final case class AuthorizeResponse(
                                    id: Long,
                                    code: Int
                                  ) extends LifesmartResponse

object AuthorizeResponse {
  implicit val format: OFormat[AuthorizeResponse] = Json.format[AuthorizeResponse]
}
