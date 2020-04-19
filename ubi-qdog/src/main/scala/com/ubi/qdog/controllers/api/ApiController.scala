package com.ubi.qdog.controllers.api

import akka.Done
import com.ubi.qdog.controllers.api.ApiResponse.{ApiResponseCode, ApiResponseCodeValue}
import com.ubi.qdog.enums.{EnumAttribute, EnumAttributeValue}
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}


trait ApiRequestData

final case class ApiRequest[T <: ApiRequestData](
  data: T
)

object ApiRequest {
  implicit def reads[T <: ApiRequestData](implicit dataReads: Reads[T]): Reads[ApiRequest[T]] = {
    Json.reads[ApiRequest[T]]
  }
}

trait ApiResponseData

case object EmptyApiResponseData extends ApiResponseData {
  implicit val format: Format[EmptyApiResponseData.type] = Format[EmptyApiResponseData.type](
    Reads[EmptyApiResponseData.type](_ => JsSuccess(EmptyApiResponseData)),
    Writes[EmptyApiResponseData.type](_ => JsObject(Seq.empty))
  )
}


trait ApiResponse {
  def code: ApiResponseCodeValue
}

object ApiResponse {

  import play.api.libs.json.{Format, JsResult, JsString, JsValue}

  /* ApiResponseCode */
  sealed abstract class ApiResponseCodeValue(
    val value: String
  ) extends EnumAttributeValue[String] {
    override def toString: String = {
      value.toString
    }
  }

  object ApiResponseCodeValue {
    implicit def format: Format[ApiResponseCodeValue] = {
      new Format[ApiResponseCodeValue] {
        override def writes(o: ApiResponseCodeValue): JsValue = {
          JsString(o.value)
        }

        override def reads(json: JsValue): JsResult[ApiResponseCodeValue] = {
          json.validate[String].flatMap(ApiResponseCode.fromValueToJsResult)
        }
      }
    }
  }

  object ApiResponseCode extends EnumAttribute[ApiResponseCodeValue] {

    case object OK extends ApiResponseCodeValue("ok")

    case object FAILED extends ApiResponseCodeValue("failed")

    protected def all: Seq[ApiResponseCodeValue] = {
      Seq[ApiResponseCodeValue](
        OK, FAILED
      )
    }
  }

}

final case class ApiResponseOk[T <: ApiResponseData](
  code: ApiResponseCodeValue,
  data: Option[T]
) extends ApiResponse

object ApiResponseOk {
  implicit def writes[T <: ApiResponseData](implicit writes: Writes[T]): OWrites[ApiResponseOk[T]] = {
    Json.writes[ApiResponseOk[T]]
  }

  def apply[T <: ApiResponseData](data: T): ApiResponseOk[T] = {
    ApiResponseOk(code = ApiResponseCode.OK, Some(data))
  }
}

final case class ApiResponseError(
  code: ApiResponseCodeValue,
  error: String
) extends ApiResponse

object ApiResponseError {
  implicit val writes: OWrites[ApiResponseError] = Json.writes[ApiResponseError]

  def apply(error: String): ApiResponseError = {
    ApiResponseError(code = ApiResponseCode.FAILED, error = error)
  }

  def apply(ex: Throwable): ApiResponseError = {
    ApiResponseError(code = ApiResponseCode.FAILED, error = ex.getLocalizedMessage)
  }
}

trait ApiController extends InjectedController with HasDatabaseConfigProvider[JdbcProfile] with I18nSupport {

  implicit def ec: ExecutionContext

  implicit class JsValueValidationHelper[T <: ApiRequestData](jsResult: JsResult[ApiRequest[T]]) {
    def withData(processor: T => Future[Result]): Future[Result] = {
      jsResult.map { apiRequest =>
        processor(apiRequest.data)
      }.recoverTotal { jsError =>
        val error = jsError.errors.map { case (path, errors) => s"${path.toString}: ${errors.map(_.messages.mkString(", ")).mkString(" ")}" }.mkString(" ")
        Future.successful(BadRequest(Json.toJson(ApiResponseError(s"[JSON PARSE ERROR] $error"))))
      }
    }
  }

  /**
   * basic helpers
   */
  implicit class ApiResponseHelper[T <: ApiResponseData](data: T)
    (implicit writes: Writes[T]) {
    def ok(
      implicit rh: RequestHeader
    ): Result = {
      Ok(Json.toJson(ApiResponseOk(data = data)))
    }
  }

  implicit class ApiDoneResponseHelper(data: Done) {
    def ok(
      implicit rh: RequestHeader
    ): Result = {
      Ok(Json.toJson(ApiResponseOk(data = EmptyApiResponseData)))
    }
  }

  implicit class ApiExceptionResponseHelper[T <: Throwable](ex: T) {
    def error(implicit rh: RequestHeader): Result = {
      BadRequest(Json.toJson(ApiResponseError(ex)))
    }
  }

  implicit class AsyncApiResponseHelper[T <: ApiResponseData](data: Future[T])
    (implicit writes: Writes[T]) {
    def ok(
      implicit rh: RequestHeader
    ): Future[Result] = {
      data.map(_.ok)
        .recover {
          case ex: Throwable => ex.error
        }
    }
  }

  implicit class AsyncApiDoneResponseHelper(data: Future[Done]) {
    def ok(
      implicit rh: RequestHeader
    ): Future[Result] = {
      data.map(_.ok).recover {
        case ex: Throwable => ex.error
      }
    }
  }

}
