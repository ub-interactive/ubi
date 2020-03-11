package controllers.api.biz.partyGiftRandomizer

import akka.stream.scaladsl.{Flow, Sink}
import cn.binarywang.wx.miniapp.api.WxMaService
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult
import controllers.api.ApiErrorHandler
import controllers.api.biz.partyGiftRandomizer.request._
import entities.sys.ImageEntryEntity
import javax.inject._
import play.api.Configuration
import play.api.libs.Files.TemporaryFileCreator
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc._
import services.biz.partyGiftRandomizer.PartyGiftRandomizerService
import services.biz.response.Code2SessionResponse
import services.sys.ImageService

import scala.concurrent.{ExecutionContext, Future}

class ApplicationController @Inject()(service: PartyGiftRandomizerService, wxMaService: WxMaService, wSClient: WSClient, configuration: Configuration, temporaryFileCreator: TemporaryFileCreator, imageService: ImageService)(implicit ec: ExecutionContext) extends InjectedController with ApiErrorHandler {

  // ws://localhost:9000/api/biz/party-gift-randomizer/connect?id=1
  def connect(id: Long): WebSocket = WebSocket.accept[String, String] { implicit request =>
    val in = Sink.ignore
    Flow.fromSinkAndSource(in, service.broadcastSource.filter(_.id == id).map(party => Json.toJson(party).toString()))
  }

  def getSessionInfo(code: String): Action[AnyContent] = Action { implicit request =>
    val result: WxMaJscode2SessionResult = wxMaService.jsCode2SessionInfo(code)
    Ok(Json.toJson(Code2SessionResponse(result.getOpenid, result.getSessionKey)))
  }

  def get: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[GetRequest] map { getRequest =>
      val GetRequest(id, openid) = getRequest
      service.get(id, openid) map {
        case Some(party) => Ok(Json.toJson(party))
        case None => NotFound
      }
    } catchErrors
  }

  def create: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreateRequest] map { createRequest =>
      val CreateRequest(openid, nickName, avatarUrl) = createRequest
      service.create(openid, nickName, avatarUrl) map { party =>
        Ok(Json.toJson(party))
      }
    } catchErrors
  }


  def qrcode(id: Long, pageOpt: Option[String]): Action[AnyContent] = Action.async { implicit request =>
    service.qrcode(id, pageOpt) map { imageEntryEntity: ImageEntryEntity =>
      Redirect(controllers.routes.UploadedAssets.at(imageEntryEntity.url))
    }
  }

  def join: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[JoinRequest] map { joinRequest =>
      val JoinRequest(id, openid, nickName, avatarUrl) = joinRequest
      service.join(id, openid, nickName, avatarUrl) map { party =>
        Ok(Json.toJson(party))
      }
    } catchErrors
  }

  def getReady: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[GetReadyRequest] map { getReadyRequest =>
      val GetReadyRequest(id, openid, height, weight) = getReadyRequest
      service.getReady(id, openid, height, weight) map { party =>
        Ok(Json.toJson(party))
      }
    } catchErrors
  }

  def start: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[StartRequest] map { startRequest =>
      val StartRequest(id, openid) = startRequest
      service.start(id, openid) map { party =>
        Ok(Json.toJson(party))
      }
    } catchErrors
  }

  def addGift: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[AddGiftRequest] map { addGiftRequest =>
      val AddGiftRequest(id, openid, giftInfo) = addGiftRequest
      val info = giftInfo.replaceAll("\uD83D\uDC49", " ").replaceAll("\uD83D\uDC48", "")
      service.addGift(id, Some(openid), info) map { party =>
        Ok(Json.toJson(party))
      }
    } catchErrors
  }

  def chooseGift: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[ChooseGiftRequest] map { chooseGiftRequest =>
      val ChooseGiftRequest(id, openid, beneficiaryId, giftId) = chooseGiftRequest
      service.chooseGift(id, openid, beneficiaryId, giftId) map { party =>
        Ok(Json.toJson(party))
      }
    } catchErrors
  }

}
