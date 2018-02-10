package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import websockets.SetWebSocket

class BridgeController @Inject()(cc: ControllerComponents)(implicit mat: Materializer, actorSystem: ActorSystem)
  extends AbstractController(cc){

  def deviceWebSocket: WebSocket = WebSocket.accept[String, String]{implicit request =>
    ActorFlow.actorRef(out => {
      SetWebSocket.props(out)
    })
  }
}
