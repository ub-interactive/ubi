package services.auth

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import daos.user.LoginInfoDao
import entities.user.{AccountEntity, LoginInfoEntity}
import play.api.mvc.RequestHeader

import scala.concurrent.Future

class AccountIdentityService @Inject()(loginInfoDAO: LoginInfoDao) extends IdentityService[AccountEntity] {

  def retrieve(loginInfo: LoginInfo): Future[Option[AccountEntity]] = Future.successful {
    loginInfoDAO.findBy("providerId" -> loginInfo.providerID, "providerKey" -> loginInfo.providerKey).flatMap(_.account.toOption)
  }

  def create(loginInfo: LoginInfo, accountEntity: AccountEntity)(implicit requestHeader: RequestHeader): Future[LoginInfoEntity] = {
    val loginInfoEntity = LoginInfoEntity(providerId = loginInfo.providerID, providerKey = loginInfo.providerKey)
    loginInfoDAO.create(loginInfoEntity)
  }

}
