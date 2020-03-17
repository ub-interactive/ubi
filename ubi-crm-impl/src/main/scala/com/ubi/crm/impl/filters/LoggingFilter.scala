package com.ubi.crm.impl.filters

import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import play.api.Logger

object LoggingFilter {
  def apply[Request, Response](serviceCall: ServerServiceCall[Request, Response])
    (implicit logger: Logger): ServerServiceCall[Request, Response] = {
    ServerServiceCall.compose { requestHeader =>
      logger.info(s"[RECEIVED] ${requestHeader.method} ${requestHeader.uri}")
      serviceCall
    }
  }
}