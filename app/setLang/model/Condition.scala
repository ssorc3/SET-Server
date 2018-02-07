package setLang.model

abstract class Condition

case class BaseCondition(op: String, left: String, right: Int) extends Condition

case class AndCondition(left: Condition, right: Condition) extends Condition
case class OrCondition(left: Condition, right: Condition) extends Condition