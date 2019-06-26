package websockets

import java.util.concurrent.ConcurrentHashMap

import akka.actor.ActorRef

import scala.collection.JavaConverters._
import scala.collection.mutable

class WebSocketManager {
  private var connections: mutable.Map[String, ActorRef] = new ConcurrentHashMap[String, ActorRef]().asScala

  def AddConnection(deviceID: String, ws: ActorRef): Unit = {
    connections += (deviceID -> ws)
  }

  def removeConnection(deviceID: String): Unit = {
    connections -= deviceID
  }

  def getConnections(): mutable.Map[String, ActorRef] = {
    connections
  }

  def getConnection(deviceID: String): Option[ActorRef] = {
    connections.get(deviceID)
  }
}

object WebSocketManager extends WebSocketManager
