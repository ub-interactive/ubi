package controllers.api.user

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import controllers.api.CrudController
import daos._
import entities.user.AccountEntity
import filters.user.AccountFilter
import play.api.libs.json
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.mvc.{Action, AnyContent}
import services.auth.authorizations.Has
import services.auth.environments.JWTEnv
import services.user.UserService
import services.user.request.AccountUpdateRequest

import scala.concurrent.ExecutionContext

class MeController @Inject()(val silhouette: Silhouette[JWTEnv],
                             val has: Has,
                             val daoService: UserService,
                            )(implicit ec: ExecutionContext) extends CrudController[AccountEntity] {

  implicit val entityFormat: json.Format[AccountEntity] = AccountEntity.format
  implicit val filterFormat: OFormat[EntityFilter[AccountEntity]] = AccountFilter.format
  implicit val resultFormat: OFormat[EntityResult[AccountEntity]] = Json.format[EntityResult[AccountEntity]]

  def profile: Action[AnyContent] = silhouette.SecuredAction { implicit request =>
    Ok(Json.toJson(request.identity))
  }

  def update: Action[JsValue] = silhouette.SecuredAction(parse.json) { implicit request =>
    request.body.validate[AccountUpdateRequest] map { accountUpdateRequest =>
      accountUpdateRequest.avatar map { avatarUrl =>
        daoService.updateAvatar(request.identity.id, avatarUrl)
      }
    }
    Ok


    // update others
//    daoService.update(request.identity.id, request.body) map {
//      case Some(result) =>
//        result fold( { errorForm =>
//          Ok(errorForm.errorString)
//        }, { entity =>
//          Ok(Json.toJson(entity))
//        })
//      case _ => NotFound
//    }
  }

}
