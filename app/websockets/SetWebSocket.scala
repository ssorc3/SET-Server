package websockets

import akka.actor.{Actor, ActorRef, Props}

class SetWebSocket(out: ActorRef) extends Actor
{
  override def receive: Receive = {
    case msg: String =>
      if(msg.startsWith("DEVICEID: "))
      {
        WebSocketManager.AddConnection(msg.substring(10), out)
      }
  }
}

object SetWebSocket {
  def props(out: ActorRef): Props = Props(new SetWebSocket(out))
}
