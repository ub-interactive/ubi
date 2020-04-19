/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.ubi.qdog.ws

import java.math.BigInteger
import java.security.MessageDigest
import java.util.UUID

import play.api.Logging
import play.api.libs.ws.{BodyWritable, WSClient, WSRequest, WSResponse}

import scala.collection.immutable.ListMap
import scala.concurrent.{ExecutionContext, Future}

/** @author liangliao at 2018/11/14 9:53 PM
 */
class WsService(
  client: WSClient
)
  (implicit executionContext: ExecutionContext) extends Logging {

  def request[REQUEST <: Request, BODY](
    endpoint: String,
    request: REQUEST
  )
    (
      implicit requestBuilder: RequestBuilder[REQUEST, BODY],
      responseParser: ResponseParser[REQUEST],
      bodyWritable: BodyWritable[BODY]
    ): Future[REQUEST#RESPONSE] = {
    val requestId: String = new BigInteger(1, MessageDigest.getInstance("MD5").digest(UUID.randomUUID.toString.getBytes)).toString(16).takeRight(4)

    val path: String = requestBuilder.path(request)
    val httpMethod: HttpMethodValue = requestBuilder.httpMethod(request)
    val queryStringParameters: ListMap[String, String] = requestBuilder.queryStringParameters(request)
    val headers: ListMap[String, String] = requestBuilder.headers(request)
    val body: BODY = requestBuilder.body(request)

    val wsRequest: WSRequest = client.url(s"$endpoint$path").withQueryStringParameters(queryStringParameters.toSeq: _*).addHttpHeaders(headers.toSeq: _*)
    val startTime: Long = System.currentTimeMillis

    val wsResponse: Future[WSResponse] = httpMethod match {
      case HttpMethod.GET =>
        logger.info(s"-> [GET/$requestId] ${wsRequest.uri.toString}")
        wsRequest.get()

      case HttpMethod.POST =>
        logger.info(s"-> [POST/$requestId] ${wsRequest.uri.toString} $body")
        wsRequest.post(body)

      case HttpMethod.PUT =>
        logger.info(s"-> [PUT/$requestId] ${wsRequest.uri.toString} $body")
        wsRequest.put(body)

      case HttpMethod.DELETE =>
        logger.info(s"-> [DELETE/$requestId] ${wsRequest.uri.toString}")
        wsRequest.delete()

      case _ => ???
    }

    wsResponse.map { response =>
      val roundTripTime: Long = System.currentTimeMillis - startTime
      response.status match {
        case 200 =>
          logger.info(s"<- [${roundTripTime}ms/$requestId] message [${response.body}]")
          responseParser.parse(response)
        case statusCode =>
          logger.error(s"<- [${roundTripTime}ms/$requestId] server response with code [$statusCode], message [${response.body}]")
          throw RequestException(statusCode, response.body, response)
      }
    }
  }
}
