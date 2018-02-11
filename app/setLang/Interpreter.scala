package setLang

import setLang.model._
import websockets.WebSocketManager
import akka.actor._
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
              devices.getUserBridges(userID).map(_.foreach(b => {
                WebSocketManager.getConnection(b) match {
                  case Some(c) => c ! "kettle"
                }
              }))
            }
            case Lights(command) => {
              devices.getUserBridges(userID).map(_.foreach(b => {
                WebSocketManager.getConnection(b) match {
                  case Some(c) => c ! "lights" + command.toString
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

      case BaseCondition(op, sensorType, threshold) =>
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
        }
    }
  }
}
