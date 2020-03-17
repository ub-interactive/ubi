package com.ubi.crm.api.user

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait UserService extends Service {

  def userGet(mobile: String): ServiceCall[NotUsed, GetUserResponse]
  def userCreate: ServiceCall[CreateUserRequest, Done]

}
