package com.ubi.ccat.controllers.api

import akka.Done
import com.ubi.ccat.controllers.api.ApiResponse.{ApiResponseCode, ApiResponseCodeValue}
import com.ubi.ccat.{EnumAttribute, EnumAttributeValue}
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc._
import slick.dbio.DBIOAction
import slick.jdbc.JdbcProfile

import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}

trait ApiRequestData

trait ApiResponseData

case object EmptyApiResponseData extends ApiResponseData {
  implicit val format: Format[EmptyApiResponseData.type] = Format[EmptyApiResponseData.type](
    Reads[EmptyApiResponseData.type](_ => JsSuccess(EmptyApiResponseData)),
    Writes[EmptyApiResponseData.type](_ => JsObject(Seq.empty))
  )
}

final case class ApiRequest[T <: ApiRequestData](
  data: T
)

object ApiRequest {
  implicit def reads[T <: ApiRequestData](implicit dataReads: Reads[T]): Reads[ApiRequest[T]] = {
    Json.reads[ApiRequest[T]]
  }
}

final case class ApiResponse[T <: ApiResponseData](
  code: ApiResponseCodeValue = ApiResponseCode.OK,
  data: Option[T],
  error: Option[String]
)

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

  def apply(error: String): ApiResponse[EmptyApiResponseData.type] = {
    new ApiResponse[EmptyApiResponseData.type](code = ApiResponseCode.FAILED, data = None, error = Some(error))
  }

  def apply[T <: ApiResponseData](data: T): ApiResponse[T] = {
    new ApiResponse[T](code = ApiResponseCode.OK, Some(data), None)
  }

  implicit def writes[T <: ApiResponseData](implicit writes: Writes[T]): OWrites[ApiResponse[T]] = {
    Json.writes[ApiResponse[T]]
  }
}

trait ApiController extends InjectedController with HasDatabaseConfigProvider[JdbcProfile] with I18nSupport {

  implicit def ec: ExecutionContext

  implicit class JsValueValidationHelper[T <: ApiRequestData](jsResult: JsResult[ApiRequest[T]]) {
    def process(processor: T => Future[Result]): Future[Result] = {
      jsResult.map { apiRequest =>
        processor(apiRequest.data)
      }.recoverTotal { jsError =>
        val error = jsError.errors.map { case (path, errors) => s"${path.toString}: ${errors.map(_.messages.mkString(", ")).mkString(" ")}" }.mkString(" ")
        Future.successful(BadRequest(Json.toJson(ApiResponse(s"[JSON PARSE ERROR] $error"))))
      }
    }
  }

  /**
   * basic helpers
   */
  implicit class DoneHelper(data: Done) {
    def ok(implicit rh: RequestHeader): Result = {
      Ok(Json.toJson(ApiResponse[EmptyApiResponseData.type](code = ApiResponseCode.OK, data = None, error = None)))
    }
  }

  implicit class ApiResponseHelper[T <: ApiResponseData](data: T)
    (implicit writes: Writes[T]) {
    def ok(implicit rh: RequestHeader): Result = {
      Ok(Json.toJson(ApiResponse[T](data = data)))
    }

    def error(implicit rh: RequestHeader): Result = {
      BadRequest(Json.toJson(ApiResponse(error = data.toString)))
    }
  }

  implicit class AsyncDBIOHelper[R <: ApiResponseData, S <: slick.dbio.NoStream, E <: slick.dbio.Effect](data: DBIOAction[R, S, E])
    (implicit writes: Writes[R]) {
    def run(implicit rh: RequestHeader): Future[Result] = {
      db.run(data).ok
    }

    def runTransactionally(implicit rh: RequestHeader): Future[Result] = {
      import profile.api._
      db.run(data.transactionally).ok
    }
  }

  implicit class AsyncApiResponseHelper[T <: ApiResponseData](data: Future[T])
    (implicit writes: Writes[T]) {
    def ok(implicit rh: RequestHeader): Future[Result] = {
      data.map(_.ok)
    }

    def error(implicit rh: RequestHeader): Future[Result] = {
      data.map(_.error)
    }
  }

}
