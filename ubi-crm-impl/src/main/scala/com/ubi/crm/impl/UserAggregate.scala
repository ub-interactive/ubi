package com.ubi.crm.impl

import java.time.Instant

import akka.actor.typed.ActorRef
import akka.cluster.sharding.typed.scaladsl.{EntityContext, EntityTypeKey}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior, ReplyEffect, RetentionCriteria}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AkkaTaggerAdapter}
import com.ubi.crm.api.enums.{UserGenderValue, UserStatusValue}
import com.ubi.crm.impl.UserAggregate.{UserCommand, UserEvent}
import play.api.libs.json._

object UserAggregate {

  trait CommandSerializable

  /* commands */
  sealed trait UserCommand extends CommandSerializable

  final case class GetUserState(
    replyTo: ActorRef[UserState]
  ) extends UserCommand

  final case class CreateUser(
    replyTo: ActorRef[Confirmation]
  )

  /* confirmation */
  sealed trait Confirmation

  final case object Accepted extends Confirmation

  final case class Rejected(reason: String) extends Confirmation

  /* events */
  sealed trait UserEvent extends AggregateEvent[UserEvent] {
    def aggregateTag: AggregateEventTag[UserEvent] = {
      UserEvent.Tag
    }
  }

  object UserEvent {
    val Tag: AggregateEventTag[UserEvent] = AggregateEventTag[UserEvent]
  }

  final case class UserCreated(
    userState: UserStatusValue,
    openId: String,
    nickname: String,
    gender: UserGenderValue,
    language: String,
    city: String,
    province: String,
    country: String,
    avatarUrl: String
  )

  object UserCreated {
    implicit val format: OFormat[UserCreated] = Json.format[UserCreated]
  }

  //
  val typeKey: EntityTypeKey[UserCommand] = EntityTypeKey[UserCommand]("User")

  def onInitialCommand(cmd: UserCommand): ReplyEffect[UserEvent, Option[UserState]] = {
    cmd match {
      case GetUserState(replyTo) => Effect.reply(replyTo)(None)
      case LinkUser(orderId, UserType.Apartment, None, replyTo) => Effect.persist(ApartmentUserLinked(orderId)).thenReply(replyTo)(_ => Accepted)
      case LinkUser(_, UserType.Apartment, Some(_), replyTo) => Effect.reply(replyTo)(Rejected("[registrationId] not allowed for apartment order"))
      case LinkUser(orderId, UserType.Hotel, Some(registrationId), replyTo) => Effect.persist(HotelUserLinked(orderId, registrationId)).thenReply(replyTo)(_ => Accepted)
      case LinkUser(_, UserType.Hotel, None, replyTo) => Effect.reply(replyTo)(Rejected("[registrationId] missing for hotel order"))

      case UnlinkUser(_, replyTo) => Effect.reply(replyTo)(Rejected(s"invalid command [${cmd.getClass.getTypeName}] sent to state [None]"))
      case LogApiInvocation(_, _, _, _, _, _, _, _, _, _) => Effect.noReply
    }
  }

  def onInitialEvent(evt: UserEvent): Option[UserState] = {
    evt match {
      case ApartmentUserLinked(orderId) => Some(ApartmentUserState(LinkedUser(orderId)))
      case HotelUserLinked(orderId, reservationId) => Some(HotelUserState(Map(reservationId -> LinkedUser(orderId))))
      case _ => throw new IllegalStateException(s"invalid event [${evt.getClass.getTypeName}] sent to state [Empty]")
    }
  }

  def apply(persistenceId: PersistenceId): EventSourcedBehavior[UserCommand, UserEvent, UserState] = {
    EventSourcedBehavior
      .withEnforcedReplies[UserCommand, UserEvent, UserState](
        persistenceId = persistenceId,
        emptyState = None,
        commandHandler = (state, cmd) => state.onCommand(cmd),
        eventHandler = (state, evt) => state.onEvent(evt)
      )
  }

  def apply(entityContext: EntityContext[UserCommand]): EventSourcedBehavior[UserCommand, UserEvent, Option[UserState]] = {
    apply(PersistenceId(entityContext.entityTypeKey.name, entityContext.entityId))
      .withTagger(AkkaTaggerAdapter.fromLagom(entityContext, UserEvent.Tag))
      .withRetention(RetentionCriteria.snapshotEvery(numberOfEvents = 100, keepNSnapshots = 2))
  }
}

final case class UserState(
  userState: UserStatusValue,
  openId: String,
  nickname: String,
  gender: UserGenderValue,
  language: String,
  city: String,
  province: String,
  country: String,
  avatarUrl: String
) {
  override def onCommand(cmd: UserCommand): ReplyEffect[UserEvent, UserState] = {
    cmd match {
      case GetUserState(replyTo) => Effect.reply(replyTo)(Some(this))
      case LinkUser(_, _, _, replyTo) => Effect.reply(replyTo)(Rejected(s"reservation already linked to apartment order [${order.orderId}]"))
      case UnlinkUser(orderId, replyTo) => Effect.persist(UserUnlinked(orderId)).thenReply(replyTo)(_ => Accepted)
      case LogApiInvocation(orderId, status, errorMessage, path, httpMethod, headers, queryStringParameters, requestBody, responseBody, rawRequest) => Effect.persist(ApiInvocationLogged(orderId, status, errorMessage, path, httpMethod, headers, queryStringParameters, requestBody, responseBody, rawRequest, Instant.now())).thenNoReply()
    }
  }

  override def onEvent(evt: UserEvent): UserState = {
    evt match {
      case _: UserUnlinked => None
      case _: ApiInvocationLogged => Some(this)
      case _ => throw new IllegalStateException(s"invalid event [${evt.getClass.getTypeName}] sent to state [$this]")
    }
  }
}

object UserState {
  implicit val format: OFormat[UserState] = Json.format[UserState]
}