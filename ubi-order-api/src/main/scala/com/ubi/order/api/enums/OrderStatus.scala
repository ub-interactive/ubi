package com.ubi.order.api.enums

import play.api.libs.json.{Format, JsResult, JsString, JsValue}

/* OrderStatus */
sealed abstract class OrderStatusValue(val value: String) extends EnumAttributeValue[String]

object OrderStatusValue {
  implicit def format: Format[OrderStatusValue] = {
    new Format[OrderStatusValue] {
      override def writes(o: OrderStatusValue): JsValue = {
        JsString(o.value)
      }

      override def reads(json: JsValue): JsResult[OrderStatusValue] = {
        json.validate[String].flatMap(OrderStatus.fromValueToJsResult)
      }
    }
  }

  //  implicit def columnMapper(implicit profile: slick.jdbc.JdbcProfile): JdbcType[OrderStatusValue] = {
  //    MappedColumnType.base[OrderStatusValue, String](_.value, OrderStatus.fromValue(_).get)
  //  }
}

object OrderStatus extends EnumAttribute[OrderStatusValue] {

  case object Created extends OrderStatusValue("created")

  protected def all: Seq[OrderStatusValue] = {
    Seq[OrderStatusValue](
      Created
    )
  }
}
