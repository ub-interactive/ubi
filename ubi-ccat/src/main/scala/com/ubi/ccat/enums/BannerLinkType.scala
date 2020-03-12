package com.ubi.ccat.enums

import com.ubi.ccat.{EnumAttribute, EnumAttributeValue}
import play.api.libs.json.{Format, JsResult, JsString, JsValue}
import play.api.mvc.QueryStringBindable.Parsing
import slick.jdbc.JdbcType

/* BannerLinkType */
sealed abstract class BannerLinkTypeValue(val value: String) extends EnumAttributeValue[String] {
  override def toString: String = {
    value
  }
}

object BannerLinkTypeValue {
  implicit def format: Format[BannerLinkTypeValue] = {
    new Format[BannerLinkTypeValue] {
      override def writes(o: BannerLinkTypeValue): JsValue = {
        JsString(o.value)
      }

      override def reads(json: JsValue): JsResult[BannerLinkTypeValue] = {
        json.validate[String].flatMap(BannerLinkType.fromValueToJsResult)
      }
    }
  }

  implicit object BannerLinkTypeBindable extends Parsing[BannerLinkTypeValue](
    param ⇒ BannerLinkType.fromValue(param).get, enum ⇒ enum.value,
    (key: String, ex: Exception) ⇒ "Cannot parse parameter %s as BannerLinkType: %s".format(key, ex.getMessage)
  )

  implicit def columnMapper(implicit profile: slick.jdbc.JdbcProfile): JdbcType[BannerLinkTypeValue] = {
    import profile.api._
    MappedColumnType.base[BannerLinkTypeValue, String](_.value, BannerLinkType.fromValue(_).get)
  }
}

object BannerLinkType extends EnumAttribute[BannerLinkTypeValue] {

  case object Subject extends BannerLinkTypeValue("subject")

  protected def all: Seq[BannerLinkTypeValue] = {
    Seq[BannerLinkTypeValue](
      Subject
    )
  }
}
