package com.ubi.crm.impl.user

import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef}
import akka.util.Timeout
import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.BadRequest
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import com.ubi.crm.api.user.{CreateUserRequest, GetUserResponse, UserService}
import com.ubi.crm.impl.filters.LoggingFilter
import com.ubi.crm.impl.user.UserAggregate.{Confirmation, CreateUser, GetUserState, UserCommand}
import play.api.{Logger, Logging}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait UserServiceImpl extends UserService with Logging {

  def clusterSharding: ClusterSharding

  def persistentEntityRegistry: PersistentEntityRegistry

  implicit def executionContext: ExecutionContext

  private def entityRef(mobile: String): EntityRef[UserCommand] = {
    clusterSharding.entityRefFor(UserAggregate.typeKey, mobile)
  }

  implicit private val askTimeout: Timeout = Timeout.durationToTimeout(5.seconds)

  private implicit val _logger: Logger = logger

  override def userGet(mobile: String): ServiceCall[NotUsed, GetUserResponse] = {
    LoggingFilter {
      ServerServiceCall { _ =>
        entityRef(mobile).ask(GetUserState).map { userState =>
          GetUserResponse(
            openId = userState.openId,
            nickname = userState.nickname,
            gender = userState.gender,
            language = userState.language,
            city = userState.city,
            province = userState.province,
            country = userState.country,
            avatarUrl = userState.avatarUrl
          )
        }
      }
    }
  }

  override def userCreate: ServiceCall[CreateUserRequest, Done] = {
    LoggingFilter {
      ServerServiceCall { createUserRequest =>
        val CreateUserRequest(mobile, openId, nickname, gender, language, city, province, country, avatarUrl) = createUserRequest
        entityRef(mobile).ask[Confirmation](replyTo => CreateUser(openId, nickname, gender, language, city, province, country, avatarUrl, replyTo)).map {
          case UserAggregate.Accepted => Done
          case UserAggregate.Rejected(reason) => throw BadRequest(reason)
        }
      }
    }
  }
}
