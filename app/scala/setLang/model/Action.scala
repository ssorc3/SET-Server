package scala.setLang.model

import scala.setLang.model.PowerSetting.PowerSetting

abstract class Action

case class Notification(body: String) extends Action
case class Alarm(command: PowerSetting) extends Action
case class Kettle(command: PowerSetting) extends Action
case class Plug(command: PowerSetting) extends Action
case class Lights(command: PowerSetting) extends Action
case class LightSetting(zone: Int, isWhite: Boolean, hue: Int, brightness: Int) extends Action

object PowerSetting extends Enumeration{
  type PowerSetting = Value

  def ON: Value = Value("on")
  def OFF: Value = Value("off")
}