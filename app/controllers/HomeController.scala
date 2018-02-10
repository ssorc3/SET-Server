package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import auth.JWTUtil
import play.api.libs.json.Json
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import websockets.{SetWebSocket, WebSocketManager}

import scala.concurrent.{ExecutionContext, Future}

class HomeController @Inject()(cc: ControllerComponents, webSocketManager: WebSocketManager, jwtUtil: JWTUtil)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends AbstractController(cc){
  def index() = Action { implicit request =>
    Ok("Hello, World!")
  }

  def test(userID: String) = Action{
    webSocketManager.getConnection(userID) match {
      case Some(connection) => {
        connection ! "Message"
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

  def secureSocketTest: WebSocket = WebSocket.acceptOrResult[String, String]{ implicit request =>
    Future.successful(request.headers.get("jw_token") match {
      case Some(token) =>
        val payload = Json.parse(jwtUtil.decodePayload(token).getOrElse(""))
        (payload \ "userID").asOpt[String] match {
          case Some(s) => Right(ActorFlow.actorRef(out => {
            WebSocketManager.AddConnection(s, out)
            SetWebSocket.props(out)
          }))
        }
      case None => Left(BadRequest)
    })
  }
}
