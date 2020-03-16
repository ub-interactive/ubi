package com.ubi.ccat.enums

import com.ubi.ccat.EnumAttributeValue
import play.api.libs.json.{Format, JsResult, JsString, JsValue}
import play.api.mvc.QueryStringBindable.Parsing
import slick.jdbc.JdbcType

/* CourseSaleType */
sealed abstract class CourseSaleTypeValue(val value: String) extends EnumAttributeValue[String] {
  override def toString: String = {
    value
  }
}

object CourseSaleTypeValue {
  implicit def format: Format[CourseSaleTypeValue] = {
    new Format[CourseSaleTypeValue] {
      override def writes(o: CourseSaleTypeValue): JsValue = {
        JsString(o.value)
      }

      override def reads(json: JsValue): JsResult[CourseSaleTypeValue] = {
        json.validate[String].flatMap(CourseSaleType.fromValueToJsResult)
      }
    }
  }

  implicit object CourseSaleTypeBindable extends Parsing[CourseSaleTypeValue](
    param => CourseSaleType.fromValue(param).get, enum => enum.value,
    (key: String, ex: Exception) => "Cannot parse parameter %s as CourseSaleType: %s".format(key, ex.getMessage)
  )

  implicit def columnMapper(implicit profile: slick.jdbc.JdbcProfile): JdbcType[CourseSaleTypeValue] = {
    import profile.api._
    MappedColumnType.base[CourseSaleTypeValue, String](_.value, CourseSaleType.fromValue(_).get)
  }
}

object CourseSaleType extends EnumAttribute[CourseSaleTypeValue] {

  case object Standard extends CourseSaleTypeValue("standard")

  case object FlashSale extends CourseSaleTypeValue("flash_sale")

  protected def all: Seq[CourseSaleTypeValue] = {
    Seq[CourseSaleTypeValue](
      Standard, FlashSale
    )
  }
}
