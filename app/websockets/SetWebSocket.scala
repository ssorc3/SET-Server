package websockets

import akka.actor.{Actor, ActorRef, Props}

class SetWebSocket(out: ActorRef) extends Actor
{
  override def receive: Receive = {
    case msg: String =>
      if(msg.startsWith("DEVICEID: "))
      {
        WebSocketManager.AddConnection(msg.substring(10), out)
        out ! "Stored your connection with deviceID: " + msg.substring(10)
      }
  }
}

object SetWebSocket {
  def props(out: ActorRef): Props = Props(new SetWebSocket(out))
}
