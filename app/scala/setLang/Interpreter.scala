package scala.setLang

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import services.ActuatorService
import scala.setLang.model._

import scala.concurrent.ExecutionContext

class Interpreter(program: List[Statement], userID: String, actuators: ActuatorService, temperatureValue: Double, humidityValue: Double,
                  lightValue: Double, noiseValue: Double)(implicit ec: ExecutionContext)
{
  def run(): Boolean = {
    var result: Boolean = false
    for (statement: Statement <- program) {
      if (walkConditional(statement.condition)) {
        for (action: Action <- statement.actions) {
          action match {
            case Email() =>
              //Can't email yet
              return false

            case Text() =>
              //Can't text yet


            case Notification(s) =>
              actuators.sendNotification(userID, s)
              result=true

            case Alarm(command) =>
              actuators.activateAlarm(userID, command)
              result=true

            case Kettle(command) =>
              actuators.changeKettlePowerSetting(userID, command)
              result = true

            case LightSetting(isWhite, hue, brightness) =>
              actuators.setLightSetting(userID, isWhite, hue, brightness)
              result=true

            case Lights(command) =>
              actuators.changeLightPowerSetting(userID, command)
              result=true

            case Plug(command) =>
              actuators.changePlugPowerSetting(userID, command)
              result=true

            case _ => println("hi")
          }
        }
      }
    }
    result
  }

  def walkConditional(condition: Condition): Boolean = {
    condition match {
      case AndCondition(a, b) =>
        walkConditional(a) && walkConditional(b)

      case OrCondition(a, b) =>
        walkConditional(a) || walkConditional(b)

      case SensorCondition(op, sensorType, threshold) =>
        val sensorValue = sensorType match {
          case "light" => lightValue
          case "temperature" => temperatureValue
          case "humidity" => humidityValue
          case "noise" => noiseValue
          case _ => 0
        }

        op match{
          case "<" => sensorValue < threshold
          case ">" => sensorValue > threshold
          case "<=" => sensorValue <= threshold
          case ">=" => sensorValue >= threshold
          case "==" => sensorValue == threshold
          case "!=" => sensorValue != threshold
        }

      case TimeCondition(op, time1, time2) =>
        val t1: DateTime = convertToDateTime(time1)
        val t2: DateTime = convertToDateTime(time2)

        op match {
          case "<" => t1.isBefore(t2)
          case ">" => t1.isAfter(t2)
          case "<=" => t1.isEqual(t2) || t1.isBefore(t2)
          case ">=" => t1.isEqual(t2) || t1.isAfter(t2)
          case "==" => t1.isEqual(t2)
          case "!=" => !t1.isEqual(t2)
        }
    }
  }

  def convertToDateTime(time: String): DateTime = time match {
    case "now" => DateTime.now
    case s =>
      val formatter: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm:ss")
      val userTime = formatter.parseLocalTime(s)
      DateTime.now.withTime(userTime)
  }
}
