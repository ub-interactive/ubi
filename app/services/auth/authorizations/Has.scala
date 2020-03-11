package services.auth.authorizations

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import entities.user.PermissionEntity.PermissionValue
import entities.user.AccountEntity

/**
  * Authorizor that hits database (potentially cache) for each permission request
  */

trait Has {
  def permission(permissions: Seq[PermissionValue]): Authorization[AccountEntity, JWTAuthenticator]

  def permission(permission: PermissionValue): Authorization[AccountEntity, JWTAuthenticator] = this.permission(Seq(permission))
}

