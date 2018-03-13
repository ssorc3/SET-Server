package scala.setLang

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import scala.setLang.model.PowerSetting.PowerSetting
import scala.setLang.model._

import scala.util.parsing.combinator.syntactical.StandardTokenParsers

class Parser extends StandardTokenParsers
{
  lexical.reserved += ("temperature", "humidity", "light", "lightSetting", "noise", "end", "if", "then", "on", "off", "email", "text", "notification", "lights", "kettle", "true", "false", "time", "now", "alarm", "plug")
  lexical.delimiters += (">", "<", ">=", "<=", "==", "!=", "&", "|", "(", ")", ";", ",")

  def program: Parser[List[Statement]] = rep(stmt)

  def stmt: Parser[Statement] = conditional ~ rep(action ~ ";") <~ "end" ^^ { case a ~ xs => Statement(a, xs.map(x => x._1))}

  def conditional: Parser[Condition] = "if" ~ "(" ~> condition <~ ")" ~ "then" ^^ { a => a }

  def condition: Parser[Condition] = andCondition | orCondition

  def andCondition: Parser[Condition] = orCondition ~ opt("&" ~ condition) ^^ {
                                          case a ~ Some("&" ~ b) => AndCondition(a, b)
                                          case a ~ None => a
                                        }

  def orCondition: Parser[Condition] = sensorCondition ~ opt("|" ~ condition) ^^ {
                                          case a ~ Some("|" ~ b) => OrCondition(a, b)
                                          case a ~ None => a
                                        }                                             |
                                        timeCondition ~ opt("|" ~ condition) ^^ {
                                          case a ~ Some("|" ~ b) => OrCondition(a, b)
                                          case a ~ None => a
                                        }

  def timeCondition: Parser[Condition] = timeString ~ operator ~ timeString  ^^ { case a ~ b ~ c => TimeCondition(b, a, c) }

  def timeString: Parser[String] = "time" ~ "(" ~> time <~ ")" ^^ { a => a }

  def time: Parser[String] = "now"  |
                              stringLit ^^ {
                                a: String =>
                                  val formatter: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm:ss")
                                  val userTime = formatter.parseLocalTime(a)
                                  val userDate = DateTime.now.withTime(userTime)

                                  if(userDate == null)
                                  {
                                    throw new IllegalArgumentException("Time string was invalid")
                                  }
                                  a
                              }

  def sensorCondition: Parser[Condition] = sensorType ~ operator ~ numericLit ^^ {case a ~ b ~ c => SensorCondition(b, a, c.toInt)}

  def sensorType: Parser[String] = "temperature"   |
                                    "humidity"     |
                                    "light"        |
                                    "noise"

  def operator: Parser[String] = ">"   |
                                  "<"  |
                                  ">=" |
                                  "<=" |
                                  "==" |
                                  "!="

  def action: Parser[Action] = "email"  ^^^ Email()                                   |
                                "text"  ^^^ Text()                                    |
                                "notification" ~> stringLit ^^ {s => Notification(s)} |
                                "kettle" ~> powerSetting ^^ {a => Kettle(a)}          |
                                "alarm" ~> powerSetting ^^ {a => Alarm(a)}            |
                                "plug" ~> powerSetting ^^ { a => Plug(a)}             |
                                "lights" ~> powerSetting ^^ { a => Lights(a)}         |
                                ("lightSetting" ~> numericLit) ~ ("," ~> bool) ~ ("," ~> numericLit) ~ ("," ~> numericLit) ^^
                                  {case a ~ b ~ c ~ d => LightSetting(a.toInt, b, c.toInt, d.toInt)}


  def powerSetting: Parser[PowerSetting] = "on"  ^^^ PowerSetting.ON   |
                                            "off" ^^^ PowerSetting.OFF

  def bool: Parser[Boolean] = "true" ^^^ true |
                              "false" ^^^ false

  def parseAll[T](parser: Parser[T], in: String): ParseResult[T] = {
    phrase(parser)(new lexical.Scanner(in))
  }
}