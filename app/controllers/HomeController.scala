package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import auth.JWTUtil
import models.DeviceRepository
import play.api.libs.json.Json
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import websockets.{SetWebSocket, WebSocketManager}

import scala.concurrent.{ExecutionContext, Future}

class HomeController @Inject()(cc: ControllerComponents, jwtUtil: JWTUtil, implicit val devices: DeviceRepository)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends AbstractController(cc){
  def index() = Action { implicit request =>
    Ok("Hello, World!")
  }

  def test(deviceID: String, message: String) = Action{
    WebSocketManager.getConnection(deviceID) match {
      case Some(connection) => {
        println("Sending message to " + deviceID)
        connection ! message
        Ok
      }
      case None => BadRequest
    }
  }

  def socketTest: WebSocket = WebSocket.accept[String, String] {implicit request =>
    ActorFlow.actorRef(out =>
      SetWebSocket.props(out)
    )
  }
}
