package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import auth.SecuredAuthenicator
import models.DeviceRepository
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import websockets.SetWebSocket

class BridgeController @Inject()(cc: ControllerComponents, auth: SecuredAuthenicator, implicit val devices: DeviceRepository)(implicit mat: Materializer, actorSystem: ActorSystem)
  extends AbstractController(cc){

  def bridgeWebSocket: WebSocket = WebSocket.accept[String, String]{implicit request =>
    ActorFlow.actorRef(out => {
      SetWebSocket.props(out)(devices)
    })
  }
}
