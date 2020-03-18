package com.ubi.crm.api

import com.lightbend.lagom.scaladsl.api.{Descriptor, Service}
import com.ubi.crm.api.user.UserService

trait CrmService extends UserService {
  this: Service =>

  override def descriptor: Descriptor = {
    import Service._
    named("ubi-crm-service").withCalls(
      pathCall("/crm/user/get?mobile", userGet _),
      pathCall("/crm/user/create", userCreate _),
    )
  }
}
