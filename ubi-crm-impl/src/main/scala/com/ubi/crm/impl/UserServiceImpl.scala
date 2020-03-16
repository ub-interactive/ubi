package com.ubi.crm.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.ubi.crm.api.{CreateUserRequest, CreateUserResponse, UserService}

class UserServiceImpl extends UserService {
  override def createUser: ServiceCall[CreateUserRequest, CreateUserResponse] = {
    ???
  }
}
