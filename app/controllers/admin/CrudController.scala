package controllers.admin

import com.mohiva.play.silhouette.api.Silhouette
import daos.{BaseDao, _}
import entities._
import entities.user.PermissionEntity
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.libs.json.OFormat
import play.api.mvc._
import play.twirl.api.Html
import services.auth.authorizations.Has
import services.auth.environments.JWTEnv

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

abstract class CrudController[T <: ActiveRecord] extends InjectedController with I18nSupport {

  val silhouette: Silhouette[JWTEnv]
  val has: Has
  val crudDAO: BaseDao[T]

  implicit val entityFormat: OFormat[T]
  implicit val filterFormat: OFormat[EntityFilter[T]]
  implicit val resultFormat: OFormat[EntityResult[T]]

  val permissions: Seq[PermissionEntity.PermissionValue] = Seq(PermissionEntity.Permission.Admin)

  def renderIndex(modelResult: EntityResult[T])(implicit request: RequestHeader): Html

  def indexCall(implicit request: RequestHeader): Call

  def renderEdit(form: Form[T])(implicit request: RequestHeader): Html

  def editCall(id: Long)(implicit request: RequestHeader): Call

  def index(pager: EntityPager, filter: EntityFilter[T], sorter: EntitySorter): Action[AnyContent] = silhouette.SecuredAction(has.permission(permissions)).async { implicit request =>
    crudDAO.search(pager, filter, sorter) map { modelResult =>
      Ok(renderIndex(modelResult))
    }
  }

  def prev(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(permissions)).async { implicit request =>
    crudDAO.prev(id) map {
      case Some(record) => Redirect(editCall(record.id))
      case _ => Redirect(editCall(id)).flashing("danger" -> "已经是第一条记录")
    }
  }

  def next(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(permissions)).async { implicit request =>
    crudDAO.next(id) map {
      case Some(record) => Redirect(editCall(record.id))
      case _ => Redirect(editCall(id)).flashing("danger" -> "已经是最后一条记录")
    }
  }

  def newPage(): Action[AnyContent] = silhouette.SecuredAction(has.permission(permissions)) { implicit request =>
    Ok(renderEdit(crudDAO.companion.form))
  }

  def create(): Action[AnyContent] = silhouette.SecuredAction(has.permission(permissions)).async { implicit request =>
    crudDAO.companion.form
      .bindFromRequest
      .fold(
        errors => Future.successful(BadRequest(renderEdit(errors))), { record =>
          crudDAO.create(record) map { record =>
            Redirect(editCall(record.id))
          }
        }
      )
  }

  def edit(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(permissions)).async { implicit request =>
    crudDAO.find(id) map {
      case Some(record) => Ok(renderEdit(crudDAO.companion.form(record)))
      case _ => Redirect(indexCall).flashing("danger" -> "记录不存在")
    }
  }

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(permissions)).async { implicit request =>
    crudDAO.find(id) flatMap {
      case Some(record) =>
        crudDAO.companion.form(record)
          .bindFromRequest
          .fold(
            errors => Future.successful(BadRequest(renderEdit(errors))), { record =>
              crudDAO.update(record) map { record =>
                Redirect(editCall(record.id))
              }

            }
          )
      case _ => Future.successful(Redirect(indexCall).flashing("danger" -> "记录不存在"))
    }
  }

  def delete(id: Long): Action[AnyContent] = silhouette.SecuredAction(has.permission(permissions)).async {
    crudDAO.find(id) flatMap {
      case Some(record) =>
        crudDAO.delete(record).map { _ =>
          Ok
        }
      case _ => Future.successful(NotFound)
    }
  }

}
