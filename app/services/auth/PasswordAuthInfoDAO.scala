package services.auth

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import entities.user.{LoginInfoEntity, PasswordInfoEntity}

import scala.concurrent.{ExecutionContext, Future}

class PasswordAuthInfoDAO @Inject()(implicit ec: ExecutionContext) extends DelegableAuthInfoDAO[PasswordInfo] {

  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = Future.successful {
    LoginInfoEntity
      .findBy("providerId" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey)
      .flatMap(_.passwordInfo.toOption)
      .map(passwordInfo => PasswordInfo(hasher = passwordInfo.hasher, password = passwordInfo.password, salt = passwordInfo.salt))
  }

  def remove(loginInfo: LoginInfo): Future[Unit] = Future.successful {
    LoginInfoEntity
      .findBy("providerId" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey)
      .flatMap(_.passwordInfo.toOption) foreach (_.delete())
  }

  def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future.successful {
    val userLoginInfo = LoginInfoEntity.findBy("providerId" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey).get
    val passwordInfo =
      PasswordInfoEntity(userLoginInfo.id, hasher = authInfo.hasher, password = authInfo.password, salt = authInfo.salt).create
    PasswordInfo(hasher = passwordInfo.hasher, password = passwordInfo.password, salt = passwordInfo.salt)
  }

  def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future.successful {
    val userLoginInfo = LoginInfoEntity.findBy("providerId" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey).get
    val userPasswordInfo = userLoginInfo.passwordInfo.toOption.get

    userPasswordInfo.password = authInfo.password
    userPasswordInfo.hasher = authInfo.hasher
    userPasswordInfo.salt = authInfo.salt
    val passwordInfo = userPasswordInfo.update
    PasswordInfo(hasher = passwordInfo.hasher, password = passwordInfo.password, salt = passwordInfo.salt)
  }

  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future.successful {
    LoginInfoEntity.findBy("providerId" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey).get
  }.flatMap { userLoginInfo =>
    userLoginInfo.passwordInfo.toOption match {
      case Some(_) => this.update(loginInfo, authInfo)
      case None => this.add(loginInfo, authInfo)
    }
  }
}
