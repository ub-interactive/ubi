package com.ubi.qdog

import com.ubi.qdog.filters.LoggingFilter
import javax.inject._
import play.api._
import play.api.http.{DefaultHttpFilters, EnabledFilters}

class Filters @Inject()(
  environment: Environment,
  defaultFilters: EnabledFilters,
  logFilter: LoggingFilter
) extends DefaultHttpFilters(defaultFilters.filters :+ logFilter: _*)