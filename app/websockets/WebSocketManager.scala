package websockets

import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}

import akka.actor.ActorRef

import collection.JavaConverters._
import scala.collection.mutable

class WebSocketManager {
  private var connections: mutable.Map[String, ActorRef] = new ConcurrentHashMap[String, ActorRef]().asScala

  def AddConnection(userID: String, ws: ActorRef): Unit = {
    connections += (userID -> ws)
  }

  def removeConnection(userID: String): Unit = {
    connections -= userID
  }

  def getConnections(): mutable.Map[String, ActorRef] = {
    connections
  }

  def getConnection(userID: String): Option[ActorRef] = {
    connections.get(userID)
  }
}

object WebSocketManager extends WebSocketManager
