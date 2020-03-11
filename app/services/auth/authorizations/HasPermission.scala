package services.auth.authorizations

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import daos.user.PermissionDao
import entities.user.PermissionEntity.PermissionValue
import entities.user.AccountEntity
import javax.inject.Inject
import play.api.mvc.Request

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author liangliao at 2018/9/13 3:03 PM
  */
class HasPermission @Inject()(permissionService: PermissionDao)(implicit ec: ExecutionContext) extends Has {
  def permission(permissions: Seq[PermissionValue]): Authorization[AccountEntity, JWTAuthenticator] = {
    new Authorization[AccountEntity, JWTAuthenticator] {
      override def isAuthorized[B](identity: AccountEntity, authenticator: JWTAuthenticator)(implicit request: Request[B]): Future[Boolean] = {
        permissionService.allOfUser(identity.id).map(_.intersect(permissions).nonEmpty)
      }
    }
  }
}
