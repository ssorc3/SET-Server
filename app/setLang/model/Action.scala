package setLang.model

import setLang.model.LightCommand.LightCommand

abstract class Action

case class Email() extends Action
case class Text() extends Action
case class Notification() extends Action
case class Kettle() extends Action
case class Lights(lightCommand: LightCommand) extends Action

object LightCommand extends Enumeration
{
  type LightCommand = Value
  val ON: Value = Value("on")
  val OFF: Value = Value("off")
  val DIM: Value = Value("dim")
}