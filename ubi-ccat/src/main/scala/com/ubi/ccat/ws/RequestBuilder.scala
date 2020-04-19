package com.ubi.ccat.ws

import scala.collection.immutable.ListMap

trait RequestBuilder[REQUEST <: Request, BODY] {
  def path(request: REQUEST): String

  def httpMethod(request: REQUEST): HttpMethodValue

  def headers(request: REQUEST): ListMap[String, String]

  def queryStringParameters(request: REQUEST): ListMap[String, String]

  def body(request: REQUEST): BODY
}

object RequestBuilder {
  def apply[REQUEST <: Request, BODY](implicit builder: RequestBuilder[REQUEST, BODY]): RequestBuilder[REQUEST, BODY] = {
    builder
  }
}

