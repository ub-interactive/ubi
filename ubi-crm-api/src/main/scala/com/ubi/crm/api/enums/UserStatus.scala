package com.ubi.crm.api.enums

import play.api.libs.json.{Format, JsResult, JsString, JsValue}

/* UserStatus */
sealed abstract class UserStatusValue(val value: String) extends EnumAttributeValue[String]

object UserStatusValue {
  implicit def format: Format[UserStatusValue] = {
    new Format[UserStatusValue] {
      override def writes(o: UserStatusValue): JsValue = {
        JsString(o.value)
      }

      override def reads(json: JsValue): JsResult[UserStatusValue] = {
        json.validate[String].flatMap(UserStatus.fromValueToJsResult)
      }
    }
  }

  //  implicit def columnMapper(implicit profile: slick.jdbc.JdbcProfile): JdbcType[UserStatusValue] = {
  //    MappedColumnType.base[UserStatusValue, String](_.value, UserStatus.fromValue(_).get)
  //  }
}

object UserStatus extends EnumAttribute[UserStatusValue] {

  case object Created extends UserStatusValue("created")

  case object Verified extends UserStatusValue("verified")

  case object Disabled extends UserStatusValue("disabled")

  protected def all: Seq[UserStatusValue] = {
    Seq[UserStatusValue](
      Created, Verified, Disabled
    )
  }
}
