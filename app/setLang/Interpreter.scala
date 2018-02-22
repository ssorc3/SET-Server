package setLang

import setLang.model._
import websockets.WebSocketManager
import akka.actor._
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import repositories.DeviceRepository

import scala.concurrent.ExecutionContext

class Interpreter(program: List[Statement], userID: String, devices: DeviceRepository,
                  temperatureValue: Double, humidityValue: Double,
                  lightValue: Double, noiseValue: Int)(implicit ec: ExecutionContext)
{
  def run(): Unit = {
    for (statement: Statement <- program) {
      if (walkConditional(statement.condition)) {
        for (action: Action <- statement.actions) {
          action match {
            case Email() => {
              //Can't email yet
            }
            case Text() => {
              //Can't text yet
            }
            case Notification() => {
              //Can't do notifications yet
            }
            case Kettle() => {
              devices.getUserBridges(userID).map(x => x.foreach(print))
              devices.getUserBridges(userID).map(_.foreach(b => {
                WebSocketManager.getConnection(b) match {
                  case Some(c) => c ! "kettle"
                  case _ =>
                }
              }))
            }
            case LightSetting(isWhite, hue, brightness) => {
              devices.getUserBridges(userID).map(_.foreach(b => {
                WebSocketManager.getConnection(b) match {
                  case Some(c) => c ! "lightSetting " + isWhite  + " " + hue + " " + brightness
                  case _ =>
                }
              }))
            }
            case Lights(command) => {
              devices.getUserBridges(userID).map(_.foreach(b => {
                WebSocketManager.getConnection(b) match {
                  case Some(c) => c ! "lights " + command
                  case _ =>
                }
              }))
            }
            case _ => println("hi")
          }
        }
      }
    }
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
