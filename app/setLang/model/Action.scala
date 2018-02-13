package setLang.model

abstract class Action

case class Email() extends Action
case class Text() extends Action
case class Notification() extends Action
case class Kettle() extends Action
case class Lights(isWhite: Boolean, hue: Int, brightness: Int) extends Action