package com.ubi.crm.api.user

import akka.Done
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait UserService extends Service {

  def userCreate: ServiceCall[CreateUserRequest, Done]

}
