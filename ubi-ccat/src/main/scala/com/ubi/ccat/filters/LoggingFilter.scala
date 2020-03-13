/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.ubi.ccat.filters

import akka.stream.Materializer
import javax.inject.Inject
import play.api.Logging
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/** @author liangliao at 2019/1/9 2:14 PM
  */

class LoggingFilter @Inject() ( implicit val mat: Materializer, ec: ExecutionContext ) extends Filter with Logging {

  def apply( nextFilter: RequestHeader ⇒ Future[Result] )( requestHeader: RequestHeader ): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter( requestHeader ).map { result ⇒
      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      logger.info( s"${result.header.status}[${requestTime}ms] - ${requestHeader.method} ${requestHeader.uri}" )

      result.withHeaders( "Request-Time" → requestTime.toString )
    }
  }
}
