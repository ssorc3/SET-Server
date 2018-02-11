package scheduledTasks

import javax.inject.Inject

import akka.actor.ActorSystem
import play.api.inject._
import repositories.{DeviceRepository, ScriptRepository, SensorDataRepository, UserRepository}
import setLang.{Interpreter, Parser}
import setLang.model.Statement

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

class ScriptTask extends SimpleModule(bind[ScriptRunner].toSelf.eagerly())

class ScriptRunner @Inject()(actorSystem: ActorSystem, scripts: ScriptRepository, sensors: SensorDataRepository, devices: DeviceRepository, users: UserRepository)(implicit executionContext: ExecutionContext) {

  actorSystem.scheduler.schedule(initialDelay = 0.seconds, interval = 10.seconds) {
    users.list.map{u =>
      u.foreach(x => runScript(x.userID))
    }
  }

  def runScript(userID: String): Unit = {
    val script: String = Await.result(scripts.getUserScript(userID), Duration.Inf).headOption.getOrElse("")
    if(script == "") return
    val temperature: Double = Await.result(sensors.getLatestUserTemperature(userID), Duration.Inf).headOption.getOrElse(0)
    val humidity: Double = Await.result(sensors.getLatestUserHumidity(userID), Duration.Inf).headOption.getOrElse(0)
    val light: Double = Await.result(sensors.getLatestUserLight(userID), Duration.Inf).headOption.getOrElse(0)
    val noise: Int = Await.result(sensors.getLatestUserNoise(userID), Duration.Inf).headOption.getOrElse(0)

    val parser: Parser = new Parser
    parser.parseAll(parser.program, script) match {
      case parser.Success(r: List[Statement], _) =>
        val interpreter: Interpreter = new Interpreter(r, userID, devices, temperature, humidity, light, noise)
        try{
          interpreter.run()
        }
        catch{
          case e: Exception => println(e.getMessage)
        }
    }
  }
}
