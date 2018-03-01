package scala.setLang.model

import scala.setLang.model.PowerSetting.PowerSetting

abstract class Action

case class Email() extends Action
case class Text() extends Action
case class Notification(body: String) extends Action
case class Alarm() extends Action
case class Kettle(command: PowerSetting) extends Action
case class Plug(command: PowerSetting) extends Action
case class Lights(command: PowerSetting) extends Action
case class LightSetting(isWhite: Boolean, hue: Int, brightness: Int) extends Action

object PowerSetting extends Enumeration{
  type PowerSetting = Value

  def ON: Value = Value("on")
  def OFF: Value = Value("off")
}