package setLang

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import services.ActuatorService
import setLang.model._

import scala.concurrent.ExecutionContext

class Interpreter(program: List[Statement], userID: String, actuators: ActuatorService, temperatureValue: Double, humidityValue: Double,
                  lightValue: Double, noiseValue: Int)(implicit ec: ExecutionContext)
{
  def run(): Boolean = {
    for (statement: Statement <- program) {
      if (walkConditional(statement.condition)) {
        for (action: Action <- statement.actions) {
          action match {
            case Email() =>
              //Can't email yet
              return false

            case Text() =>
              //Can't text yet
              return false

            case Notification() =>
              //Can't do notifications yet
              return false

            case Alarm() =>
              actuators.activateAlarm(userID)
              return true

            case Kettle(command) =>
              actuators.changeKettlePowerSetting(userID, command)
              return true

            case LightSetting(isWhite, hue, brightness) =>
              actuators.setLightSetting(userID, isWhite, hue, brightness)
              return true

            case Lights(command) =>
              actuators.changeLightPowerSetting(userID, command)
              return true

            case Plug(command) =>
              actuators.changePlugPowerSetting(userID, command)
              return true

            case _ => println("hi")
          }
        }
      }
    }
    false
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
    case s: String =>
      val formatter: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm:ss")
            formatter.parseDateTime(s)
  }
}
