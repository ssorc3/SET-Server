package setLang.model

import setLang.model.LightCommand.LightCommand

abstract class Action

case class Email() extends Action
case class Text() extends Action
case class Notification() extends Action
case class Kettle() extends Action
case class Lights(command: LightCommand) extends Action
case class LightSetting(isWhite: Boolean, hue: Int, brightness: Int) extends Action

object LightCommand extends Enumeration{
  type LightCommand = Value

  def ON: Value = Value
  def OFF: Value = Value
}