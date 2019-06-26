package scala.setLang.model

abstract class Condition

case class SensorCondition(op: String, left: String, right: Int) extends Condition
case class TimeCondition(op: String, time1: String, time2: String) extends Condition

case class AndCondition(left: Condition, right: Condition) extends Condition
case class OrCondition(left: Condition, right: Condition) extends Condition