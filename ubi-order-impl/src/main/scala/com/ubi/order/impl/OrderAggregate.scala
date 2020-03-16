package com.ubi.order.impl

import java.time.Instant
import java.util.UUID

import akka.actor.typed.ActorRef
import akka.cluster.sharding.typed.scaladsl.{EntityContext, EntityTypeKey}
import akka.http.scaladsl.model.HttpMethod
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior, ReplyEffect, RetentionCriteria}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AkkaTaggerAdapter}
import com.ubi.order.api.enums.OrderStatusValue
import com.ubi.order.impl.OrderAggregate.{OrderCommand, OrderEvent}
import play.api.libs.json._

import scala.collection.immutable.ListMap

object OrderAggregate {

  trait CommandSerializable

  /* commands */
  sealed trait OrderCommand extends CommandSerializable

  final case class GetOrderState(
    replyTo: ActorRef[Option[OrderState]]
  ) extends OrderCommand

  final case class LinkOrder(
    orderId: String, /* use String for future compatibility to use UUID */
    orderType: OrderTypeValue,
    registrationId: Option[String],
    replyTo: ActorRef[Confirmation]
  ) extends OrderCommand

  final case class UnlinkOrder(
    orderId: String,
    replyTo: ActorRef[Confirmation]
  ) extends OrderCommand

  final case class LogApiInvocation(
    orderId: String,
    status: ApiInvocationStatusValue,
    errorMessage: Option[String],
    path: String,
    httpMethod: HttpMethod,
    headers: ListMap[String, String],
    queryStringParameters: ListMap[String, String],
    requestBody: JsValue,
    responseBody: Option[JsValue],
    rawRequest: JsValue
  ) extends OrderCommand

  /* confirmation */
  sealed trait Confirmation

  final case object Accepted extends Confirmation

  final case class Rejected(reason: String) extends Confirmation

  /* events */
  sealed trait OrderEvent extends AggregateEvent[OrderEvent] {
    def aggregateTag: AggregateEventTag[OrderEvent] = {
      OrderEvent.Tag
    }
  }

  object OrderEvent {
    val Tag: AggregateEventTag[OrderEvent] = AggregateEventTag[OrderEvent]
  }

  final case class ApartmentOrderLinked(
    orderId: String
  ) extends OrderEvent

  object ApartmentOrderLinked {
    implicit val format: OFormat[ApartmentOrderLinked] = Json.format[ApartmentOrderLinked]
  }

  final case class HotelOrderLinked(
    orderId: String,
    registrationId: String
  ) extends OrderEvent

  object HotelOrderLinked {
    implicit val format: OFormat[HotelOrderLinked] = Json.format[HotelOrderLinked]
  }

  final case class OrderUnlinked(
    orderId: String
  ) extends OrderEvent

  case object OrderUnlinked {
    implicit val format: OFormat[OrderUnlinked] = Json.format[OrderUnlinked]
  }

  final case class ApiInvocationLogged(
    orderId: String,
    status: ApiInvocationStatusValue,
    errorMessage: Option[String],
    path: String,
    httpMethod: HttpMethod,
    headers: ListMap[String, String],
    queryStringParameters: ListMap[String, String],
    requestBody: JsValue,
    responseBody: Option[JsValue],
    rawRequest: JsValue,
    ts: Instant
  ) extends OrderEvent

  object ApiInvocationLogged extends ListMapJsonFormatSupport with HttpMethodJsonFormatSupport {
    implicit val format: OFormat[ApiInvocationLogged] = Json.format[ApiInvocationLogged]
  }

  //
  val typeKey: EntityTypeKey[OrderCommand] = EntityTypeKey[OrderCommand]("Order")

  def onInitialCommand(cmd: OrderCommand): ReplyEffect[OrderEvent, Option[OrderState]] = {
    cmd match {
      case GetOrderState(replyTo) => Effect.reply(replyTo)(None)
      case LinkOrder(orderId, OrderType.Apartment, None, replyTo) => Effect.persist(ApartmentOrderLinked(orderId)).thenReply(replyTo)(_ => Accepted)
      case LinkOrder(_, OrderType.Apartment, Some(_), replyTo) => Effect.reply(replyTo)(Rejected("[registrationId] not allowed for apartment order"))
      case LinkOrder(orderId, OrderType.Hotel, Some(registrationId), replyTo) => Effect.persist(HotelOrderLinked(orderId, registrationId)).thenReply(replyTo)(_ => Accepted)
      case LinkOrder(_, OrderType.Hotel, None, replyTo) => Effect.reply(replyTo)(Rejected("[registrationId] missing for hotel order"))

      case UnlinkOrder(_, replyTo) => Effect.reply(replyTo)(Rejected(s"invalid command [${cmd.getClass.getTypeName}] sent to state [None]"))
      case LogApiInvocation(_, _, _, _, _, _, _, _, _, _) => Effect.noReply
    }
  }

  def onInitialEvent(evt: OrderEvent): Option[OrderState] = {
    evt match {
      case ApartmentOrderLinked(orderId) => Some(ApartmentOrderState(LinkedOrder(orderId)))
      case HotelOrderLinked(orderId, reservationId) => Some(HotelOrderState(Map(reservationId -> LinkedOrder(orderId))))
      case _ => throw new IllegalStateException(s"invalid event [${evt.getClass.getTypeName}] sent to state [Empty]")
    }
  }

  def apply(persistenceId: PersistenceId): EventSourcedBehavior[OrderCommand, OrderEvent, Option[OrderState]] = {
    EventSourcedBehavior
      .withEnforcedReplies[OrderCommand, OrderEvent, Option[OrderState]](
        persistenceId = persistenceId,
        emptyState = None,
        commandHandler = (state, cmd) => state match {
          case None => onInitialCommand(cmd)
          case Some(value) => value.onCommand(cmd)
        },
        eventHandler = (state, evt) => state match {
          case None => onInitialEvent(evt)
          case Some(value) => value.onEvent(evt)
        }
      )
  }

  def apply(entityContext: EntityContext[OrderCommand]): EventSourcedBehavior[OrderCommand, OrderEvent, Option[OrderState]] = {
    apply(PersistenceId(entityContext.entityTypeKey.name, entityContext.entityId))
      .withTagger(AkkaTaggerAdapter.fromLagom(entityContext, OrderEvent.Tag))
      .withRetention(RetentionCriteria.snapshotEvery(numberOfEvents = 100, keepNSnapshots = 2))
  }
}

final case class OrderState(
  orderId: UUID,
  skuId: UUID,
  orderStatus: OrderStatusValue,
  price: Int,
  quantity: Int,
  totalAmount: Int
) {
  override def onCommand(cmd: OrderCommand): ReplyEffect[OrderEvent, OrderState] = {
    cmd match {
      case GetOrderState(replyTo) => Effect.reply(replyTo)(Some(this))
      case LinkOrder(_, _, _, replyTo) => Effect.reply(replyTo)(Rejected(s"reservation already linked to apartment order [${order.orderId}]"))
      case UnlinkOrder(orderId, replyTo) => Effect.persist(OrderUnlinked(orderId)).thenReply(replyTo)(_ => Accepted)
      case LogApiInvocation(orderId, status, errorMessage, path, httpMethod, headers, queryStringParameters, requestBody, responseBody, rawRequest) => Effect.persist(ApiInvocationLogged(orderId, status, errorMessage, path, httpMethod, headers, queryStringParameters, requestBody, responseBody, rawRequest, Instant.now())).thenNoReply()
    }
  }

  override def onEvent(evt: OrderEvent): Option[OrderState] = {
    evt match {
      case _: OrderUnlinked => None
      case _: ApiInvocationLogged => Some(this)
      case _ => throw new IllegalStateException(s"invalid event [${evt.getClass.getTypeName}] sent to state [$this]")
    }
  }
}

object OrderState {
  implicit val format: OFormat[OrderState] = Json.format[OrderState]
}