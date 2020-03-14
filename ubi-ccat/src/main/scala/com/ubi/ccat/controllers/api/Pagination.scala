package com.ubi.ccat.controllers.api

import play.api.libs.json.{JsNumber, JsObject, Writes}
import play.api.mvc.QueryStringBindable


final case class PaginationParameter(
  page: Int,
  size: Int
) {
  val offset: Int = (page - 1) * size
  val limit: Int = size
}

object PaginationParameter {
  implicit def queryStringBindable(implicit intBinder: QueryStringBindable[Int]): QueryStringBindable[PaginationParameter] = {
    new QueryStringBindable[PaginationParameter] {
      override def bind(
        key: String,
        params: Map[String, Seq[String]]
      ): Option[Either[String, PaginationParameter]] = {
        for {
          page <- intBinder.bind(key + ".curr", params)
          size <- intBinder.bind(key + ".size", params)
        } yield for {
          p <- page
          s <- size
        } yield PaginationParameter(p, s)
      }

      override def unbind(
        key: String,
        value: PaginationParameter
      ): String = {
        Seq(
          intBinder.unbind(key + ".curr", value.page),
          intBinder.unbind(key + ".size", value.size)
        ).mkString("&")
      }
    }
  }
}

final case class PaginationResponseData(
  page: Int,
  size: Int,
  totalRecords: Int
)

object PaginationResponseData {
  implicit val writes: Writes[PaginationResponseData] = (o: PaginationResponseData) => {
    JsObject(Seq(
      "page" -> JsNumber(o.page),
      "size" -> JsNumber(o.size),
      "pages" -> JsNumber(o.totalRecords / o.size)
    ))
  }
}
