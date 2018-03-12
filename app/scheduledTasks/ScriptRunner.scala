package scheduledTasks

import javax.inject.Inject

import akka.actor.ActorSystem
import play.api.inject._
import repositories.{ScriptRepository, SensorDataRepository, UserRepository}
import services.{ActuatorService, ScriptService}

import scala.setLang.model.Statement
import scala.setLang.{Interpreter, Parser}
import websockets.WebSocketManager

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

class ScriptTask extends SimpleModule(bind[ScriptRunner].toSelf.eagerly())

class ScriptRunner @Inject()(actorSystem: ActorSystem, scripts: ScriptRepository,
                             sensors: SensorDataRepository, actuators: ActuatorService,
                             scriptRunner: ScriptService, users: UserRepository)(implicit executionContext: ExecutionContext) {

  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 2.seconds) {
    users.list.map{u =>
      u.foreach(x => {
        println("running script \"default\" for " + x.username)
        scriptRunner.runScript(x.userID, "default")
      })

    }
  }

  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 10.seconds)
  {
    WebSocketManager.getConnections().foreach(x => x._2 ! "heartbeat")
  }
}
