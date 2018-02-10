package websockets

import akka.actor.{Actor, ActorRef, PoisonPill, Props}

class SetWebSocket(out: ActorRef) extends Actor
{
  var bridgeID = ""

  override def receive: Receive = {
    case msg: String =>
      if(msg.startsWith("BRIDGEID: "))
      {
        bridgeID = msg.substring(10)
        WebSocketManager.AddConnection(msg.substring(10), out)
        out ! "Stored your connection with deviceID: " + msg.substring(10)
      }
      else {
        if(bridgeID == "")
        {
          out ! "Invalid Protocol: Start communication with \"BRIDGEID: <BRIDGEID>\""
          out ! PoisonPill
        }
      }
  }

  override def postStop(): Unit = {
    super.postStop()
    if(bridgeID != "")
      WebSocketManager.removeConnection(bridgeID)
  }
}

object SetWebSocket {
  def props(out: ActorRef): Props = Props(new SetWebSocket(out))
}
