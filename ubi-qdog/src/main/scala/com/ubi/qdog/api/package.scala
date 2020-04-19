/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.stey.connect.lifesmart.impl

import java.security.MessageDigest

import com.stey.common.ws._
import play.api.libs.json.{Format, JsString, JsValue, _}

import scala.collection.immutable.ListMap

package object api {

  trait LifesmartRequest extends
                         Request

  trait LifesmartCommandRequest extends
                                LifesmartRequest {
    override def path: String = {
      s"/api.$method"
    }

    override def httpMethod: HttpMethodValue = {
      HttpMethod.POST
    }

    def params: Map[String, String] = {
      Map.empty
    }

    def method: String
  }

  trait LifesmartResponse extends
                          Response

  trait LifesmartCommandResponse[T] extends
                                    LifesmartResponse {
    val id: Long
    val code: ResponseCodeValue
    val message: T
  }

  final case class LifesmartApiException(
                                          code: ResponseCodeValue,
                                          message: String
                                        ) extends VendorException(code.value.toString, message)
                                          with LifesmartResponse

  object LifesmartApiException {
    implicit val format: OFormat[LifesmartApiException] = Json.format[LifesmartApiException]
  }

  final case class LifesmartCommandApiException(
                                                 code: ResponseCodeValue,
                                                 message: String
                                               ) extends VendorException(code.value.toString, message)
                                                 with LifesmartResponse

  object LifesmartCommandApiException {
    implicit val format: OFormat[LifesmartCommandApiException] = Json.format[LifesmartCommandApiException]
  }

  class LifesmartRequestBuilder[T <: LifesmartRequest](implicit wrts: Writes[T]) extends RequestBuilder[T, JsValue] {
    override def path(r: T): String = {
      r.path
    }

    override def httpMethod(r: T): HttpMethodValue = {
      r.httpMethod
    }

    override def headers(r: T): ListMap[String, String] = {
      r.headers
    }

    override def queryStringParameters(r: T): ListMap[String, String] = {
      r.queryStringParameters
    }

    override def body(r: T): JsValue = {
      Json.toJson(r)
    }
  }

  class LifesmartCommandRequestBuilder[T <: LifesmartCommandRequest](
                                                                      userId: String,
                                                                      userToken: String,
                                                                      appDid: String,
                                                                      appKey: String,
                                                                      appToke: String,
                                                                      svrUrl: String
                                                                    )
                                                                    (implicit wrts: Writes[T]) extends LifesmartRequestBuilder[T] {

    override def body(r: T): JsValue = {
      val timestamp: Long = System.currentTimeMillis / 1000
      val signParams: Seq[(String, String)] = Seq("method" -> r.method) ++ r.params.toSeq.sortBy(_._1) ++ Seq(
        "did" -> appDid,
        "time" -> timestamp.toString,
        "userid" -> userId,
        "usertoken" -> userToken,
        "appkey" -> appKey,
        "apptoken" -> appToke
        )
      val paramString = signParams.map { case (key, value) => s"$key:$value" }.mkString(",")
      val sign = MessageDigest.getInstance("MD5").digest(paramString.getBytes).map(0xFF & _).map("%02x".format(_)).foldLeft("")(_ + _)

      val paramJsValueOpt = Json.toJson(r.params) match {
        case JsNull => None
        case value => Some(value)
      }

      val values = Map(
        "id" -> Some(JsNumber(timestamp)),
        "method" -> Some(JsString(r.method)),
        "system" -> Some(Json.toJson(CommandRequestSystem(userid = userId, did = appDid, time = timestamp, appkey = appKey, sign = sign))),
        "params" -> paramJsValueOpt
        ).collect { case (key, Some(value)) => key -> value }

      JsObject(values)
    }
  }

  /* for command request */
  final case class Command[T <: LifesmartCommandRequest](
                                                          id: Long,
                                                          method: String,
                                                          system: CommandRequestSystem,
                                                          params: Option[T]
                                                        )

  object Command {
    implicit def writes[T <: LifesmartCommandRequest](implicit wrts: Writes[T]): Writes[Command[T]] = {
      (o: Command[T]) => Json.writes[Command[T]].writes(o)
    }

    implicit def reads[T <: LifesmartCommandRequest](implicit rds: Reads[T]): Reads[Command[T]] = {
      (json: JsValue) =>
        JsSuccess(
          new Command(
            id = (json \ "id").as[Long],
            method = (json \ "method").as[String],
            system = (json \ "system").as[CommandRequestSystem],
            params = (json \ "params").asOpt[T]
            )
          )
    }
  }

  final case class CommandRequestSystem(
                                         ver   : String,
                                         lang  : String,
                                         userid: String,
                                         did   : String,
                                         time  : Long,
                                         appkey: String,
                                         sign  : String
                                       )

  object CommandRequestSystem {
    def apply(
               userid: String,
               did   : String,
               time  : Long,
               appkey: String,
               sign  : String
             ): CommandRequestSystem = {
      new CommandRequestSystem("1.0", "en", userid, did, time, appkey, sign)
    }

    implicit val format: OFormat[CommandRequestSystem] = Json.format[CommandRequestSystem]
  }

  final case class CommandResponse[T](
                                       id     : Long,
                                       code   : Int,
                                       message: T
                                     )

  object CommandResponse {
    implicit def format[T](implicit fmt: Format[T]): Format[CommandResponse[T]] = {
      Json.format[CommandResponse[T]]
    }
  }

}
