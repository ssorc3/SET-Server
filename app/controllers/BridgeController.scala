package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import auth.SecuredAuthenticator
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import repositories.DeviceRepository
import websockets.SetWebSocket

import scala.concurrent.ExecutionContext

class BridgeController @Inject()(cc: ControllerComponents, auth: SecuredAuthenticator, implicit val devices: DeviceRepository)(implicit mat: Materializer, actorSystem: ActorSystem, ec: ExecutionContext)
  extends AbstractController(cc){

  def bridgeWebSocket: WebSocket = WebSocket.accept[String, String]{implicit request =>
    ActorFlow.actorRef(out => {
      SetWebSocket.props(out)
    })
  }
}
