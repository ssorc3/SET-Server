package services

import javax.inject.{Inject, Singleton}

import repositories.{ScriptRepository, SensorDataRepository}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.setLang.{Interpreter, Parser}
import scala.setLang.model.Statement

@Singleton
class ScriptService @Inject()(scripts: ScriptRepository, sensors: SensorDataRepository, actuators: ActuatorService)(implicit ec: ExecutionContext)
{
  def runScript(userID: String, scriptName: String, motion: Boolean, zone: Int): Unit = {
    val script: String = Await.result(scripts.getUserScript(userID, scriptName), Duration.Inf).headOption.getOrElse("")
    val lastRun: Long = Await.result(scripts.getUserLastRun(userID, scriptName), Duration.Inf).headOption.getOrElse(0L)
    if(!motion) {
      if (script == "" || ((System.currentTimeMillis() / 1000) - lastRun) < 12 * 60 * 60) {
        return
      }
    }
    val temperature: Double = Await.result(sensors.getLatestUserTemperature(userID), Duration.Inf).headOption.getOrElse(0)
    val humidity: Double = Await.result(sensors.getLatestUserHumidity(userID), Duration.Inf).headOption.getOrElse(0)
    val light: Double = Await.result(sensors.getLatestUserLight(userID), Duration.Inf).headOption.getOrElse(0)
    val noise: Double = Await.result(sensors.getLatestUserNoise(userID), Duration.Inf).headOption.getOrElse(0)

    val parser: Parser = new Parser
    parser.parseAll(parser.program, script) match {
      case parser.Success(r: List[Statement], _) =>
        val interpreter: Interpreter = new Interpreter(r, userID, actuators, temperature, humidity, light, noise, zone)
        try{
          if(interpreter.run()) {
            println("Script ran successfully")
            scripts.updateLastRun(userID, System.currentTimeMillis())
          }
        }
        catch{
          case e: Exception => println(e.getMessage)
        }
      case _ =>
    }
  }
}
