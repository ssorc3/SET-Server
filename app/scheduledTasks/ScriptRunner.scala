package scheduledTasks

import javax.inject.Inject

import akka.actor.ActorSystem
import play.api.inject._
import repositories.{ScriptRepository, SensorDataRepository, UserRepository}
import services.ActuatorService
import setLang.model.Statement
import setLang.{Interpreter, Parser}
import websockets.WebSocketManager

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

class ScriptTask extends SimpleModule(bind[ScriptRunner].toSelf.eagerly())

class ScriptRunner @Inject()(actorSystem: ActorSystem, scripts: ScriptRepository, sensors: SensorDataRepository, actuators: ActuatorService, users: UserRepository)(implicit executionContext: ExecutionContext) {

  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 10.seconds) {
    users.list.map{u =>
      u.foreach(x => runScript(x.userID))
      WebSocketManager.getConnections().foreach(x => x._2 ! "heartbeat")
    }
  }

  def runScript(userID: String): Unit = {
    val script: String = Await.result(scripts.getUserScript(userID), Duration.Inf).headOption.getOrElse("")
    val lastRun: Long = Await.result(scripts.getUserLastRun(userID), Duration.Inf).headOption.getOrElse(0L)
    if(script == "" || (System.currentTimeMillis()/1000) - lastRun < 24*60*60) return
    val temperature: Double = Await.result(sensors.getLatestUserTemperature(userID), Duration.Inf).headOption.getOrElse(0)
    val humidity: Double = Await.result(sensors.getLatestUserHumidity(userID), Duration.Inf).headOption.getOrElse(0)
    val light: Double = Await.result(sensors.getLatestUserLight(userID), Duration.Inf).headOption.getOrElse(0)
    val noise: Int = Await.result(sensors.getLatestUserNoise(userID), Duration.Inf).headOption.getOrElse(0)

    val parser: Parser = new Parser
    parser.parseAll(parser.program, script) match {
      case parser.Success(r: List[Statement], _) =>
        val interpreter: Interpreter = new Interpreter(r, userID, actuators, temperature, humidity, light, noise)
        try{
          interpreter.run()
          scripts.updateLastRun(userID, System.currentTimeMillis())
        }
        catch{
          case e: Exception => println(e.getMessage)
        }
    }
  }
}
