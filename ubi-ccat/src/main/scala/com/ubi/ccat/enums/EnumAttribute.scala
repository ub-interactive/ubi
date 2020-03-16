package com.ubi.ccat.enums

import play.api.libs.json.{JsError, JsResult, JsSuccess}

/** @author liangliao at 2018/9/14 4:25 PM
 */
trait EnumAttributeValue[R] {
  type RAW = R

  def value: R
}

abstract class EnumAttribute[E <: EnumAttributeValue[_]] extends Seq[E] {

  protected def all: Seq[E]

  override def length: Int = {
    all.length
  }

  override def apply(idx: Int): E = {
    all(idx)
  }

  override def iterator: Iterator[E] = {
    all.iterator
  }

  def fromValue[T](value: T): Option[E] = {
    value match {
      case v: String => all.find(_.value match {
        case value: String => value.compareToIgnoreCase(v) == 0
        case _ => false
      })
      case _ => all.find(_.value == value)
    }
  }

  def fromValueToJsResult[T](value: T): JsResult[E] = {
    fromValue(value) match {
      case Some(enumValue) => JsSuccess(enumValue)
      case None => JsError(s"invalid code [$value]")
    }
  }
}

abstract class GroupedEnumAttribute[E <: EnumAttributeValue[_]] {

  def group: Seq[EnumAttribute[E]]

  def fromValue[T](value: T): Option[E] = {
    group.flatMap(_.fromValue(value)).headOption
  }

  def fromValueToJsResult[T](value: T): JsResult[E] = {
    group
      .map(_.fromValueToJsResult(value))
      .find(_.isSuccess)
      .getOrElse(JsError(s"invalid value [$value]"))
  }
}
