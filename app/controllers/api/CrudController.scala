package controllers.api

import com.mohiva.play.silhouette.api.Silhouette
import daos._
import entities._
import entities.user.PermissionEntity.{Permission, PermissionValue}
import play.api.libs.json._
import play.api.mvc._
import services.BaseDaoService
import services.auth.authorizations.Has
import services.auth.environments.JWTEnv

import scala.concurrent.ExecutionContext.Implicits._

abstract class CrudController[T <: ActiveRecord] extends InjectedController with ApiErrorHandler {

  val silhouette: Silhouette[JWTEnv]
  val has: Has
  val daoService: BaseDaoService[T]

  val permissions: Seq[PermissionValue] = Seq(Permission.Admin)

  implicit val entityFormat: Format[T]
  implicit val filterFormat: Format[EntityFilter[T]]
  implicit val resultFormat: Format[EntityResult[T]]

  def list(pager: EntityPager, filter: EntityFilter[T], sorter: EntitySorter): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    daoService.list(pager, filter, sorter) map { modelResult =>
      Ok(Json.toJson(modelResult))
    }
  }

  def create: Action[JsValue] = silhouette.SecuredAction.async(parse.json) { implicit request =>
    daoService.create(request.body) map { result =>
      result fold( { errorForm =>
        Ok(errorForm.errorString)
      }, { entity =>
        Ok(Json.toJson(entity))
      })
    }
  }

  def get(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    daoService.get(id) map {
      case Some(record) => Ok(Json.toJson(record))
      case _ => NotFound
    }
  }

  def update(id: Long): Action[JsValue] = silhouette.SecuredAction.async(parse.json) { implicit request =>
    daoService.update(id, request.body) map {
      case Some(result) =>
        result fold( { errorForm =>
          Ok(errorForm.errorString)
        }, { entity =>
          Ok(Json.toJson(entity))
        })
      case _ => NotFound
    }
  }

  def delete(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    daoService.delete(id) map { success =>
      Ok
    }
  }

  def next(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    daoService.next(id) map {
      case Some(record) => Ok(Json.toJson(record))
      case _ => NotFound
    }
  }

  def prev(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    daoService.prev(id) map {
      case Some(record) => Ok(Json.toJson(record))
      case _ => NotFound
    }
  }
}
