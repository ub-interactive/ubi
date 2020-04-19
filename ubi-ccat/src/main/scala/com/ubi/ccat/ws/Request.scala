package com.ubi.ccat.ws

import scala.collection.immutable.ListMap

/**
  * @author liangliao at 2018/10/10 1:07 AM
  */
trait Request {

  type RESPONSE <: Response

  def path: String

  def httpMethod: HttpMethodValue = {
    HttpMethod.GET
  }

  def headers: ListMap[String, String] = {
    ListMap.empty
  }

  def queryStringParameters: ListMap[String, String] = {
    ListMap.empty
  }
}
