package com.ubi.crm.api.enums

import play.api.libs.json.{Format, JsResult, JsString, JsValue}

/* UserGender */
sealed abstract class UserGenderValue(val value: String) extends EnumAttributeValue[String]

object UserGenderValue {
  implicit def format: Format[UserGenderValue] = {
    new Format[UserGenderValue] {
      override def writes(o: UserGenderValue): JsValue = {
        JsString(o.value)
      }

      override def reads(json: JsValue): JsResult[UserGenderValue] = {
        json.validate[String].flatMap(UserGender.fromValueToJsResult)
      }
    }
  }

  //  implicit def columnMapper(implicit profile: slick.jdbc.JdbcProfile): JdbcType[UserGenderValue] = {
  //    MappedColumnType.base[UserGenderValue, String](_.value, UserGender.fromValue(_).get)
  //  }
}

object UserGender extends EnumAttribute[UserGenderValue] {

  case object Male extends UserGenderValue("male")

  case object Female extends UserGenderValue("female")

  case object Secret extends UserGenderValue("secret")

  protected def all: Seq[UserGenderValue] = {
    Seq[UserGenderValue](
      Male, Female, Secret
    )
  }
}
