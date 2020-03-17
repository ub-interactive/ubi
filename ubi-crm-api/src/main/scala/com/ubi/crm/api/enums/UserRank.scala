package com.ubi.crm.api.enums

import play.api.libs.json.{Format, JsResult, JsString, JsValue}

/* UserRank */
sealed abstract class UserRankValue(val value: String) extends EnumAttributeValue[String]

object UserRankValue {
  implicit def format: Format[UserRankValue] = {
    new Format[UserRankValue] {
      override def writes(o: UserRankValue): JsValue = {
        JsString(o.value)
      }

      override def reads(json: JsValue): JsResult[UserRankValue] = {
        json.validate[String].flatMap(UserRank.fromValueToJsResult)
      }
    }
  }

  //  implicit def columnMapper(implicit profile: slick.jdbc.JdbcProfile): JdbcType[UserRankValue] = {
  //    MappedColumnType.base[UserRankValue, String](_.value, UserRank.fromValue(_).get)
  //  }
}

object UserRank extends EnumAttribute[UserRankValue] {

  case object R1 extends UserRankValue("r1")

  case object R2 extends UserRankValue("r2")

  case object R3 extends UserRankValue("r3")

  protected def all: Seq[UserRankValue] = {
    Seq[UserRankValue](
      R1, R2, R3
    )
  }
}
