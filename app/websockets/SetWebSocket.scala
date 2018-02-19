package websockets

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import repositories.DeviceRepository

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
          case _ =>
        }
        out ! "Stored your connection with bridgeID: " + bridgeID
        out ! WebSocketManager.getConnections()
      }
      else {
        if(bridgeID.isEmpty)
        {
          out ! "Invalid Protocol: Start communication with \"BRIDGEID: <BRIDGEID>\""
          out ! PoisonPill
        }
      }
  }

  override def postStop(): Unit = {
    super.postStop()
    if(!bridgeID.isEmpty)
      WebSocketManager.removeConnection(bridgeID)
  }
}

object SetWebSocket {
  def props(out: ActorRef)(implicit devices: DeviceRepository, ec: ExecutionContext): Props = Props(new SetWebSocket(out))
}
