package com.ubi.ccat.enums

import play.api.libs.json.{Format, JsResult, JsString, JsValue}
import play.api.mvc.QueryStringBindable.Parsing
import slick.jdbc.JdbcType

/* SubjectDisplayStyle */
sealed abstract class SubjectDisplayStyleValue(val value: String) extends EnumAttributeValue[String] {
  override def toString: String = {
    value
  }
}

object SubjectDisplayStyleValue {
  implicit def format: Format[SubjectDisplayStyleValue] = {
    new Format[SubjectDisplayStyleValue] {
      override def writes(o: SubjectDisplayStyleValue): JsValue = {
        JsString(o.value)
      }

      override def reads(json: JsValue): JsResult[SubjectDisplayStyleValue] = {
        json.validate[String].flatMap(SubjectDisplayStyle.fromValueToJsResult)
      }
    }
  }

  implicit object SubjectDisplayStyleBindable extends Parsing[SubjectDisplayStyleValue](
    param => SubjectDisplayStyle.fromValue(param).get, enum => enum.value,
    (key: String, ex: Exception) => "Cannot parse parameter %s as SubjectDisplayStyle: %s".format(key, ex.getMessage)
  )

  implicit def columnMapper(implicit profile: slick.jdbc.JdbcProfile): JdbcType[SubjectDisplayStyleValue] = {
    import profile.api._
    MappedColumnType.base[SubjectDisplayStyleValue, String](_.value, SubjectDisplayStyle.fromValue(_).get)
  }
}

object SubjectDisplayStyle extends EnumAttribute[SubjectDisplayStyleValue] {

  case object OneColumn extends SubjectDisplayStyleValue("one_column")

  case object TwoColumn extends SubjectDisplayStyleValue("two_column")

  protected def all: Seq[SubjectDisplayStyleValue] = {
    Seq[SubjectDisplayStyleValue](
      OneColumn, TwoColumn
    )
  }
}
