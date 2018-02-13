package setLang

import setLang.model.LightCommand.LightCommand
import setLang.model._

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

class Parser extends StandardTokenParsers
{
  lexical.reserved += ("temperature", "humidity", "light", "noise", "end", "if", "then", "on", "off", "dim", "email", "text", "notification", "lights", "kettle")
  lexical.delimiters += (">", "<", ">=", "<=", "==", "&", "|", "(", ")", ";")

  def program: Parser[List[Statement]] = rep(stmt)

  def stmt: Parser[Statement] = conditional ~ rep(action ~ ";") <~ "end" ^^ { case a ~ xs => Statement(a, xs.map(x => x._1))}

  def conditional: Parser[Condition] = "if" ~ "(" ~> condition <~ ")" ~ "then" ^^ { a => a }

  def condition: Parser[Condition] = andCondition | orCondition

  def andCondition: Parser[Condition] = orCondition ~ opt("&" ~ condition) ^^ {
                                          case a ~ Some("&" ~ b) => AndCondition(a, b)
                                          case a ~ None => a
                                        }

  def orCondition: Parser[Condition] = baseCondition ~ opt("|" ~ condition) ^^ {
                                          case a ~ Some("|" ~ b) => OrCondition(a, b)
                                          case a ~ None => a
                                        }

  def baseCondition: Parser[Condition] = sensorType ~ operator ~ numericLit ^^ {case a ~ b ~ c => BaseCondition(b, a, c.toInt)}

  def sensorType: Parser[String] = "temperature" |
                                    "humidity"   |
                                    "light"      |
                                    "noise"

  def operator: Parser[String] = ">"   |
                                  "<"  |
                                  ">=" |
                                  "<=" |
                                  "=="

  def action: Parser[Action] = "email"  ^^^ Email()                          |
                                "text"  ^^^ Text()                           |
                                "notification" ^^^ Notification()            |
                                "kettle" ^^^ Kettle()                        |
                                ("lights" ~> bool) ~ (", " ~> numericLit) ~ (", " ~> numericLit) ^^ {case a ~ b ~ c => Lights(a, b.toInt, c.toInt)}


  def bool: Parser[Boolean] = "true" ^^^ true |
                              "false" ^^^ false

  def parseAll[T](parser: Parser[T], in: String): ParseResult[T] = {
    phrase(parser)(new lexical.Scanner(in))
  }

}
