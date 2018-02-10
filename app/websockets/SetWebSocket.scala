package websockets

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import models.DeviceRepository

import scala.concurrent.ExecutionContext

class SetWebSocket(out: ActorRef)(implicit devices: DeviceRepository, ec: ExecutionContext) extends Actor
{
  var bridgeID = ""

  override def receive: Receive = {
    case msg: String =>
      if(msg.startsWith("BRIDGEID: "))
      {
        bridgeID = msg.substring(10)
        WebSocketManager.AddConnection(bridgeID, out)
        devices.isBridge(bridgeID).map{
          case false => devices.setAsBridge(bridgeID)
        }
        out ! "Stored your connection with bridgeID: " + bridgeID
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
  def props(out: ActorRef)(implicit devices: DeviceRepository, ec: ExecutionContext): Props = Props(new SetWebSocket(out))
}
