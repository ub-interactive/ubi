package com.ubi.controllers.api

import play.api.data.Form
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

trait ApiErrorHandler {
  this: InjectedController =>

  implicit class FormHelper(errorForm: Form[_]) {
    def errorString: String = errorForm.errors.map(error => s"${error.key}:${error.messages.mkString(",")}").mkString("; ")
  }

  implicit class JsAsyncHelper(jsResult: JsResult[Future[Result]]) {
    def catchErrors(implicit request: RequestHeader): Future[Result] = {
      jsResult recoverTotal { jsError: JsError =>
        val errorSeq = jsError.errors map {
          case (path, errors) => path.toString -> errors.map(_.toString).mkString(" ")
        }
        Future.successful(Ok(Json.toJson(errorSeq)))
      }
    } recover {
      case x: Throwable =>
        x.printStackTrace()
        InternalServerError(x.getLocalizedMessage)
    }
  }

}