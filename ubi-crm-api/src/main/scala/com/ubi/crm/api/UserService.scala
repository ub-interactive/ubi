package com.ubi.crm.api

import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait UserService extends Service {

  def createUser: ServiceCall[CreateUserRequest, CreateUserResponse]

  override def descriptor: Descriptor = {
    import Service._
    named("ubi-crm-service").withCalls(
      pathCall("/crm/user/create", createUser _),
    )
  }
}
