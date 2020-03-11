package com.ubi.api.web

import javax.inject._
import play.api._
import play.api.http.HttpFilters
import play.api.mvc.EssentialFilter
import play.filters.cors.CORSFilter
import play.filters.gzip.GzipFilter
import play.filters.headers.SecurityHeadersFilter

@Singleton
class Filters @Inject()(
  environment: Environment,
  corsFilter: CORSFilter,
  securityHeadersFilter: SecurityHeadersFilter,
  gzipFilter: GzipFilter
) extends HttpFilters {

  override val filters: Seq[EssentialFilter] = {
    if (environment.mode == Mode.Dev) Seq.empty else {
      Seq(gzipFilter, corsFilter)
    }
  }

}
