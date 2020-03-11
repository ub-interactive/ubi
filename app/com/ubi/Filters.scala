package com.ubi

import com.mohiva.play.htmlcompressor.HTMLCompressorFilter
import com.mohiva.play.xmlcompressor.XMLCompressorFilter
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
  htmlCompressorFilter: HTMLCompressorFilter,
  xmlCompressorFilter: XMLCompressorFilter,
  gzipFilter: GzipFilter
) extends HttpFilters {

  override val filters: Seq[EssentialFilter] = {
    if (environment.mode == Mode.Dev) Seq.empty else {
      Seq(gzipFilter, htmlCompressorFilter, xmlCompressorFilter, corsFilter)
    }
  }

}
