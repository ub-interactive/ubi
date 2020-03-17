package com.ubi.crm.impl.user

import akka.actor.typed.ActorRef
import akka.cluster.sharding.typed.scaladsl.{EntityContext, EntityTypeKey}
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior, ReplyEffect, RetentionCriteria}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AkkaTaggerAdapter}
import com.ubi.crm.api.enums._
import com.ubi.crm.impl.user.UserAggregate.{GetUserState, UserCommand, UserEvent}
import play.api.libs.json._

object UserAggregate {

  trait CommandSerializable

  /* commands */
  sealed trait UserCommand extends CommandSerializable

  final case class GetUserState(
    replyTo: ActorRef[UserState]
  ) extends UserCommand

  final case class CreateUser(
    openId: String,
    nickname: String,
    gender: UserGenderValue,
    language: String,
    city: String,
    province: String,
    country: String,
    avatarUrl: String,
    replyTo: ActorRef[Confirmation]
  ) extends UserCommand

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
    openId: String,
    nickname: String,
    gender: UserGenderValue,
    language: String,
    city: String,
    province: String,
    country: String,
    avatarUrl: String,
  ) extends UserEvent

  object UserCreated {
    implicit val format: OFormat[UserCreated] = Json.format[UserCreated]
  }

  //
  val typeKey: EntityTypeKey[UserCommand] = EntityTypeKey[UserCommand]("User")

  def onInitialCommand(cmd: UserCommand): ReplyEffect[UserEvent, Option[UserState]] = {
    cmd match {
      case CreateUser(openId, nickname, gender, language, city, province, country, avatarUrl, replyTo) =>
        Effect.persist(UserCreated(openId, nickname, gender, language, city, province, country, avatarUrl)).thenReply(replyTo)(_ => Accepted)
      case cmd => throw new IllegalStateException(s"invalid command [${cmd.getClass.getTypeName}] sent to state [None]")
    }
  }

  def onInitialEvent(evt: UserEvent): Option[UserState] = {
    evt match {
      case UserCreated(openId, nickname, gender, language, city, province, country, avatarUrl) => Some(UserState(
        state = UserStatus.Created,
        rank = UserRank.R1,
        openId = openId,
        nickname = nickname,
        gender = gender,
        language = language,
        city = city,
        province = province,
        country = country,
        avatarUrl = avatarUrl
      ))
      case _ => throw new IllegalStateException(s"invalid event [${evt.getClass.getTypeName}] sent to state [None]")
    }
  }

  def apply(persistenceId: PersistenceId): EventSourcedBehavior[UserCommand, UserEvent, Option[UserState]] = {
    EventSourcedBehavior
      .withEnforcedReplies[UserCommand, UserEvent, Option[UserState]](
        persistenceId = persistenceId,
        emptyState = None,
        commandHandler = (state, cmd) => state match {
          case Some(value) => value.onCommand(cmd)
          case None => onInitialCommand(cmd)
        },
        eventHandler = (state, evt) => state match {
          case Some(value) => value.onEvent(evt)
          case None => onInitialEvent(evt)
        }
      )
  }

  def apply(entityContext: EntityContext[UserCommand]): EventSourcedBehavior[UserCommand, UserEvent, Option[UserState]] = {
    apply(PersistenceId(entityContext.entityTypeKey.name, entityContext.entityId))
      .withTagger(AkkaTaggerAdapter.fromLagom(entityContext, UserEvent.Tag))
      .withRetention(RetentionCriteria.snapshotEvery(numberOfEvents = 100, keepNSnapshots = 2))
  }
}

final case class UserState(
  state: UserStatusValue,
  rank: UserRankValue,
  openId: String,
  nickname: String,
  gender: UserGenderValue,
  language: String,
  city: String,
  province: String,
  country: String,
  avatarUrl: String
) {
  def onCommand(cmd: UserCommand): ReplyEffect[UserEvent, Option[UserState]] = {
    cmd match {
      case GetUserState(replyTo) => Effect.reply(replyTo)(this)
      case cmd => throw new IllegalStateException(s"invalid command [${cmd.getClass.getTypeName}]")
    }
  }

  def onEvent(evt: UserEvent): Option[UserState] = {
    evt match {
      case evt => throw new IllegalStateException(s"invalid event [${evt.getClass.getTypeName}]")
    }
  }
}

object UserState {
  implicit val format: OFormat[UserState] = Json.format[UserState]
}