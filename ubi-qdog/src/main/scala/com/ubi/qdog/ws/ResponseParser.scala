/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.ubi.qdog.ws

import com.mthaler.xmlconfect.{XmlElemReader, _}
import play.api.Logging
import play.api.libs.json.{JsError, JsSuccess, Reads}
import play.api.libs.ws.WSResponse

import scala.util.{Failure, Success, Try}

/** @author liangliao at 2018/10/10 1:07 AM
  */
trait ResponseParser[T <: Request] {
  def parse(response: WSResponse): T#RESPONSE
}

class JsValueResponseParser[T <: Request](implicit rds: Reads[T#RESPONSE]) extends ResponseParser[T] {
  override def parse(response: WSResponse): T#RESPONSE = {
    response.json.validate[T#RESPONSE] match {
      case JsSuccess(value, _) => value
      case JsError(errors) =>
        val errorMsg = errors.map {
          case (jsPath, jsonValidationErrors) => jsPath.toString -> jsonValidationErrors.map(_.messages.mkString(", ")).mkString(" ")
        }
        throw ParseErrorException(response, errorMsg.toString)
    }
  }
}

object JsValueResponseParser {
  def apply[T <: Request](implicit rds: Reads[T#RESPONSE]) = {
    new JsValueResponseParser[T]
  }
}

class XmlResponseParser[T <: Request](implicit reader: XmlElemReader[T#RESPONSE]) extends ResponseParser[T] with Logging {
  override def parse(response: WSResponse): T#RESPONSE = {
    Try(response.xml.convertTo[T#RESPONSE]) match {
      case Success(value) => value
      case Failure(exception) => throw ParseErrorException(response, s"failed to parse xml with exception [${exception.getLocalizedMessage}]")
    }
  }
}

object XmlResponseParser {
  def apply[T <: Request](implicit reader: XmlElemReader[T#RESPONSE]) = {
    new XmlResponseParser[T]
  }
}
