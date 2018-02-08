package setLang

import setLang.model._

class Interpreter(program: List[Statement], temperatureValue: Double, humidityValue: Double, lightValue: Double, noiseValue: Int)
{
  def run(): Unit = {
    for (statement: Statement <- program) {
      if (walkConditional(statement.condition)) {
        for (action: Action <- statement.actions) {
          action match {
            case Email() => println("Email")                      //TODO: Dispatch email
            case Text() => println("Text")                        //TODO: Send Text
            case Notification() => println("Notification")        //TODO: Send Notification
            case Kettle() => println("Kettle")                    //TODO: Turn on Kettle
            case Lights(command) => println("Lights " + command)  //TODO: Send command to lights
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
