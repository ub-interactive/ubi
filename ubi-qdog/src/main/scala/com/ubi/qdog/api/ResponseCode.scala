package com.stey.connect.lifesmart.impl.api

import com.lightbend.lagom.scaladsl.api.deser.PathParamSerializer
import com.stey.connect.lifesmart.api.enums.{EnumAttribute, EnumAttributeValue}
import play.api.libs.json.{Format, JsNumber, JsResult, JsValue}

/* ResponseCode */
final case class ResponseCodeValue(value: Int) extends EnumAttributeValue[Int] {
  override def toString: String = {
    value.toString
  }
}

object ResponseCodeValue {
  /* json formats */
  implicit def format: Format[ResponseCodeValue] = {
    new Format[ResponseCodeValue] {
      override def writes(o: ResponseCodeValue): JsValue = {
        JsNumber(o.value)
      }

      override def reads(json: JsValue): JsResult[ResponseCodeValue] = {
        json.validate[Int].flatMap(ResponseCode.fromValueToJsResult)
      }
    }
  }

  /* implicit conversions from raw value */
  implicit def fromValue(v: Int): ResponseCodeValue = {
    ResponseCode.fromValue(v).get
  }

  implicit def toValue(o: ResponseCodeValue): Int = {
    o.value
  }

  /* lagom path param serializer */
  implicit object ResponseCodePathParamSerializer extends PathParamSerializer[ResponseCodeValue] {
    override def serialize(parameter: ResponseCodeValue): Seq[String] = {
      Seq(parameter.value.toString)
    }

    override def deserialize(parameters: Seq[String]): ResponseCodeValue = {
      parameters.headOption.flatMap(_.toIntOption).flatMap(ResponseCode.fromValue(_)).get
    }
  }

}


object ResponseCode extends EnumAttribute[ResponseCodeValue] {

  val C0: ResponseCodeValue = ResponseCodeValue(0) // "成功"

  val C10001: ResponseCodeValue = ResponseCodeValue(10001) // "请求格式错误"

  val C10002: ResponseCodeValue = ResponseCodeValue(10002) // "Appkey不存在"

  val C10003: ResponseCodeValue = ResponseCodeValue(10003) // "不支持http GET请求"

  val C10004: ResponseCodeValue = ResponseCodeValue(10004) // "签名非法"

  val C10005: ResponseCodeValue = ResponseCodeValue(10005) // "用户没有授权"

  val C10006: ResponseCodeValue = ResponseCodeValue(10006) // "用户授权超时"

  val C10007: ResponseCodeValue = ResponseCodeValue(10007) // "非法访问"

  val C10008: ResponseCodeValue = ResponseCodeValue(10008) // "内部错误"

  val C10009: ResponseCodeValue = ResponseCodeValue(10009) // "设置属性失败"

  val C10010: ResponseCodeValue = ResponseCodeValue(10010) // "Method非法"

  val C10011: ResponseCodeValue = ResponseCodeValue(10011) // "操作超时"

  val C10012: ResponseCodeValue = ResponseCodeValue(10012) // "用户名已存在"

  val C10013: ResponseCodeValue = ResponseCodeValue(10013) // "设备没准备好"

  val C10014: ResponseCodeValue = ResponseCodeValue(10014) // "设备已被其他账户注册"

  val C10015: ResponseCodeValue = ResponseCodeValue(10015) // "权限不够"

  val C10016: ResponseCodeValue = ResponseCodeValue(10016) // "设备不支持该操作"

  val C10017: ResponseCodeValue = ResponseCodeValue(10017) // "数据非法"

  val C10018: ResponseCodeValue = ResponseCodeValue(10018) // "GPS位置非法访问拒绝"

  val C10019: ResponseCodeValue = ResponseCodeValue(10019) // "请求对象不存在"

  val C10020: ResponseCodeValue = ResponseCodeValue(10020) // "设备已经在账户中"

  protected def all: Seq[ResponseCodeValue] = {
    Seq[ResponseCodeValue](
      C0,
      C10001,
      C10002,
      C10003,
      C10004,
      C10005,
      C10006,
      C10007,
      C10008,
      C10009,
      C10010,
      C10011,
      C10012,
      C10013,
      C10014,
      C10015,
      C10016,
      C10017,
      C10018,
      C10019,
      C10020
      )
  }
}