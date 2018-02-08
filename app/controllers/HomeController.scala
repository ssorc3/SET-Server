package controllers

import javax.inject.Inject

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.concurrent.ExecutionContext

class HomeController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, ec: ExecutionContext) extends AbstractController(cc){
  def index() = Action { implicit request =>
    Ok("Hello, World!")
  }

  def socketTest = WebSocket.accept[String, String]{ implicit request =>
    ActorFlow.actorRef{ out =>
      WebSocketActor.props(out)
    }
  }
}

object WebSocketActor{
  def props(out: ActorRef) = Props(new WebSocketActor(out))
}

class WebSocketActor(out: ActorRef) extends Actor{
  def receive = {
    case msg: String =>
      out ! "I received your message:" + msg
  }
}
