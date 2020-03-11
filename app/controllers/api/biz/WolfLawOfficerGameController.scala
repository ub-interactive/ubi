package controllers.api.biz

import akka.stream.scaladsl.{Flow, Sink}
import controllers.api.ApiErrorHandler
import javax.inject._
import models.biz.wolfLawOfficerGame.Game.Skill
import models.biz.wolfLawOfficerGame.{Game, GameSetting}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.biz.WolfLawOfficerGameService

import scala.concurrent.{ExecutionContext, Future}

class WolfLawOfficerGameController @Inject()(gameService: WolfLawOfficerGameService)(implicit ec: ExecutionContext) extends InjectedController with ApiErrorHandler {

  // ws://localhost:9000/api/biz/wolf-law-officer/connect?id=1fd4
  def connect(id: String): WebSocket = WebSocket.accept[String, String] { implicit request =>
    val in = Sink.ignore
    Flow.fromSinkAndSource(in, gameService.broadcastSource.filter(_.id.equalsIgnoreCase(id)).map(game => Json.toJson(game).toString()))
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[GameSetting] fold( { _ =>
      Future.successful(InternalServerError)
    }, { gameSetting =>
      gameService.create(gameSetting) map { game =>
        Ok(Json.toJson(game))
      }
    })
  }

  def join(id: String): Action[AnyContent] = Action.async { implicit request =>
    gameService.withGame(id, game => game.state == Game.State.New && game.seats.exists(_.player.isEmpty)) { game =>
      game
    } map { game =>
      Ok(Json.toJson(game))
    }
  }

  def sit(id: String, seatNumber: Int, userId: String, userName: String): Action[AnyContent] = Action.async { implicit request =>
    gameService.withGame(id, game => game.state == Game.State.New) { game =>
      gameService.sit(game, seatNumber, userId, userName)
    } map {
      game => Ok(Json.toJson(game))
    }
  }

  def shuffle(id: String): Action[AnyContent] = Action.async { implicit request =>
    gameService.withGame(id, game => game.state == Game.State.New && game.seats.forall(_.player.isDefined)) { game =>
      gameService.shuffle(game)
    } map { game =>
      Ok(Json.toJson(game))
    }
  }

  def action(id: String, userId: String, targetUserId: String, skill: String): Action[AnyContent] = Action.async { implicit request =>
    Skill.fromString(skill) match {
      case Some(skillValue) => gameService.withGame(id, game => game.state == Game.State.New || game.state == Game.State.InProgress) { game =>
        gameService.action(game, userId, targetUserId, skillValue)
      } map { game =>
        Ok(Json.toJson(game))
      }
      case _ => throw new Exception("game started")
    }
  }

  def nextTurn(id: String): Action[AnyContent] = Action.async { implicit request =>
    gameService.withGame(id, game => (game.state == Game.State.New || game.state == Game.State.InProgress) && game.seats.forall(_.player.isDefined)) { game =>
      gameService.nextStep(game)
    } map { game =>
      Ok(Json.toJson(game))
    }
  }

}
