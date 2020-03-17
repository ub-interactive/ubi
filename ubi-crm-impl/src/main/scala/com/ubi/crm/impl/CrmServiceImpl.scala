package com.ubi.crm.impl

import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.ubi.crm.api.CrmService
import com.ubi.crm.impl.user.UserServiceImpl

import scala.concurrent.ExecutionContext

class CrmServiceImpl(
  val clusterSharding: ClusterSharding,
  val persistentEntityRegistry: PersistentEntityRegistry
)
  (implicit val executionContext: ExecutionContext) extends CrmService with UserServiceImpl
