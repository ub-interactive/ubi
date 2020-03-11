///*
// * Copyright (C) 2017-2019 Stey Inc. <https://www.stey.com>
// */
//
//package com.ubi.api
//
//import play.api.UsefulException
//import play.api.i18n.I18nSupport
//import play.api.libs.json._
//import play.api.mvc._
//import slick.dbio.DBIOAction
//
//import scala.concurrent.{ExecutionContext, Future}
//import scala.reflect.runtime.universe._
//
//trait ApiDto
//
//final case class ApiRequest[T: TypeTag](
//  data: T,
//  timestamp: Long
//)
//
//object ApiRequest {
//  implicit def reads[T: TypeTag](implicit dataReads: Reads[T]): Reads[ApiRequest[T]] = Json.reads[ApiRequest[T]]
//}
//
//final case class ApiResponse[T: TypeTag](
//  data: Option[T],
//  error: Option[String]
//)
//
//object ApiResponse {
//  def apply(error: String): ApiResponse[String] = new ApiResponse[String](None, Some(error))
//
//  def apply[T: TypeTag](data: T): ApiResponse[T] = new ApiResponse[T](Some(data), None)
//
//  implicit def writes[T: TypeTag](implicit dataWrites: Writes[T]): OWrites[ApiResponse[T]] = Json.writes[ApiResponse[T]]
//}
//
//final case class SeqEitherDto[L <: UsefulException, T](
//  message: Seq[L],
//  success: Seq[T]
//) extends ApiDto
//
//object SeqEitherDto {
//  def apply[L <: UsefulException, R](data: Seq[Either[L, R]]): SeqEitherDto[L, R] = {
//    data.foldLeft(SeqEitherDto[L, R](message = Seq.empty, success = Seq.empty)) { (accum, either) =>
//      either match {
//        case Left(message)  => accum.copy(message = accum.message :+ message)
//        case Right(success) => accum.copy(success = accum.success :+ success)
//      }
//    }
//  }
//
//  implicit def writes[L <: UsefulException, R](implicit rWrits: Writes[R]): Writes[SeqEitherDto[L, R]] =
//    (o: SeqEitherDto[L, R]) => JsObject(Map(
//      "message" -> Json.toJson(o.message.map(_.description)),
//      "success" -> Json.toJson(o.success)
//    ))
//}
//
//trait ApiController extends InjectedController with DBIOService with I18nSupport {
//
//  implicit def ec: ExecutionContext
//
//  implicit class JsValueValidationHelper(jsResult: JsResult[Future[Result]]) {
//    def catchErrors(implicit rh: RequestHeader): Future[Result] =
//      jsResult.recoverTotal { jsError =>
//        val errorSeq = jsError.errors.map { case (path, errors) => path.toString -> errors.map(_.messages.mkString(", ")).mkString(" ") }
//        Future.successful(BadRequest(Json.toJson(ApiResponse(error = Some("invalid json"), data = Some(errorSeq.toMap)))))
//      }
//  }
//
//  /* option */
//  implicit class OptionHelper[T: TypeTag](data: Option[T]) {
//    def ok(implicit dataWrites: Writes[T], rh: RequestHeader): Result = data match {
//      case Some(value) => value.ok
//      case None        => "NotFound".error
//    }
//  }
//
//  implicit class AsyncOptionHelper[T: TypeTag](data: Future[Option[T]]) {
//    def ok(implicit dataWrites: Writes[T], rh: RequestHeader): Future[Result] = data.map(_.ok)
//  }
//
//  /* json writers */
//  implicit val UnitTypeWrites: Writes[Unit.type] = (o: Unit.type) => JsString("Ok")
//
//  implicit val UnitWrites: Writes[Unit] = (o: Unit) => JsString("Ok")
//
//  implicit val AkkaDoneWrites: Writes[akka.Done] = (o: akka.Done) => JsString("Ok")
//
//  /** basic helpers
//    */
//  implicit class ApiResponseHelper[T: TypeTag](data: T)(implicit writes: Writes[T]) {
//
//    def ok(implicit rh: RequestHeader): Result = Ok(Json.toJson(ApiResponse[T](data = data)))
//
//    def error(implicit rh: RequestHeader): Result = Ok(Json.toJson(ApiResponse(error = data.toString)))
//  }
//
//  implicit class AsyncDBIOHelper[R: TypeTag, S <: slick.dbio.NoStream, E <: slick.dbio.Effect](data: DBIOAction[R, S, E])(implicit writes: Writes[R]) {
//    def run(implicit rh: RequestHeader): Future[Result] = db.run(data).ok
//
//    def runTransactionally(implicit rh: RequestHeader): Future[Result] = db.run(data.transactionally).ok
//  }
//
//  implicit class AsyncApiResponseHelper[T: TypeTag](data: Future[T])(implicit writes: Writes[T]) {
//    def ok(implicit rh: RequestHeader): Future[Result] = data.map(_.ok)
//
//    def error(implicit rh: RequestHeader): Future[Result] = data.map(_.error)
//  }
//
//  /** Actions
//    */
//  private def PermissionActionFilter(allowedPermissions: Set[PermissionModel.PermissionValue]): ActionFilter[SecuredRequest] =
//    new ActionFilter[SecuredRequest] {
//      implicit val executionContext: ExecutionContext = ec
//
//      override def filter[A](request: SecuredRequest[A]): Future[Option[Result]] = Future.successful {
//        if (request.identity.permissions.intersect(allowedPermissions).isEmpty) {
//          Some("user permissions has no access to this resource".error(request))
//        }
//        else {
//          None
//        }
//      }
//    }
//
//  def AuthorizedAction(allowedPermissions: Set[PermissionModel.PermissionValue])(implicit securedAction: SecuredAction): ActionBuilder[SecuredRequest, AnyContent] =
//    securedAction.andThen(PermissionActionFilter(allowedPermissions))
//
//}
