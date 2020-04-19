/*
 * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
 */

package com.ubi.ccat.ws

import play.api.PlayException
import play.api.libs.ws.WSResponse

/** @author liangliao at 2019-03-26 19:50
 */
abstract class VendorException(
  title: String,
  description: String
) extends PlayException(title, description)

final case class RequestException(
  statusCode: Int,
  message: String,
  response: WSResponse
) extends PlayException("RequestException", message)

final case class ParseErrorException(
  response: WSResponse,
  message: String
) extends PlayException("ParseError", message)