package scala.setLang

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import services.ActuatorService

import setLang.model.Statement

class TimeSpec extends PlaySpec with MockitoSugar{
  "The parser" should {
    "correctly parse time strings in the form HH:mm:ss" in {
      var result: Boolean = true
      val script: String = "if(time(\"16:00:00\") == time(\"16:00:00\"))then end"
      val parser: Parser = new Parser
      parser.parseAll(parser.program, script) match {
        case parser.Success(r: List[Statement], _) =>
        case parser.Error(msg, n)  => result = false
        case parser.Failure(msg, n) => result = false
      }
    }

    "throw an error when time strings are incorrect" in {
      var result: Boolean = true
      val script: String = "if(time(now) == time(\"10:00\"))then end"
      val parser: Parser = new Parser
      a[java.lang.IllegalArgumentException] must be thrownBy parser.parseAll(parser.program, script)
    }
  }

  "The interpreter" should {
    "evaluate time(\"00:00:00\") == time(\"00:00:00\") to true" in {
      val script: String = "if(time(\"00:00:00\") == time(\"00:00:00\"))then alarm; end"
      val actuators = mock[ActuatorService]
      val parser: Parser = new Parser
      parser.parseAll(parser.program, script) match {
        case parser.Success(r: List[Statement], _) =>
          val interpreter: Interpreter = new Interpreter(r, "", actuators, 0, 0, 0, 0)
          interpreter.run mustBe true
      }
    }

    "evaluate time(\"00:00:00\") == time(\"00:00:10\") to false" in {
      val script: String = "if(time(\"00:00:00\") == time(\"00:00:10\"))then alarm; end"
      val actuators = mock[ActuatorService]
      val parser: Parser = new Parser
      parser.parseAll(parser.program, script) match {
        case parser.Success(r: List[Statement], _) =>
          val interpreter: Interpreter = new Interpreter(r, "", actuators, 0, 0, 0, 0)
          interpreter.run mustBe false
      }
    }

      "evaluate time(\"00:00:00\") < time(\"00:00:10\") to true" in {
        val script: String = "if(time(\"00:00:00\") < time(\"00:00:10\"))then alarm; end"
        val actuators = mock[ActuatorService]
        val parser: Parser = new Parser
        parser.parseAll(parser.program, script) match {
          case parser.Success(r: List[Statement], _) =>
            val interpreter: Interpreter = new Interpreter(r, "", actuators, 0, 0, 0, 0)
            interpreter.run mustBe true
        }
      }

    "evaluate time(\"00:00:10\") < time(\"00:00:00\") to false" in {
        val script: String = "if(time(\"00:00:10\") < time(\"00:00:00\"))then alarm; end"
        val actuators = mock[ActuatorService]
        val parser: Parser = new Parser
        parser.parseAll(parser.program, script) match {
          case parser.Success(r: List[Statement], _) =>
            val interpreter: Interpreter = new Interpreter(r, "", actuators, 0, 0, 0, 0)
            interpreter.run mustBe false
        }
      }

      "evaluate time(\"00:00:00\") > time(\"00:00:10\") to false" in {
        val script: String = "if(time(\"00:00:00\") > time(\"00:00:10\"))then alarm; end"
        val actuators = mock[ActuatorService]
        val parser: Parser = new Parser
        parser.parseAll(parser.program, script) match {
          case parser.Success(r: List[Statement], _) =>
            val interpreter: Interpreter = new Interpreter(r, "", actuators, 0, 0, 0, 0)
            interpreter.run mustBe false
        }
      }

    "evaluate time(\"00:00:10\") > time(\"00:00:00\") to true" in {
        val script: String = "if(time(\"00:00:10\") > time(\"00:00:00\"))then alarm; end"
        val actuators = mock[ActuatorService]
        val parser: Parser = new Parser
        parser.parseAll(parser.program, script) match {
          case parser.Success(r: List[Statement], _) =>
            val interpreter: Interpreter = new Interpreter(r, "", actuators, 0, 0, 0, 0)
            interpreter.run mustBe true
        }
      }

      "evaluate time(\"00:00:00\") >= time(\"00:00:00\") to true" in {
        val script: String = "if(time(\"00:00:00\") >= time(\"00:00:00\"))then alarm; end"
        val actuators = mock[ActuatorService]
        val parser: Parser = new Parser
        parser.parseAll(parser.program, script) match {
          case parser.Success(r: List[Statement], _) =>
            val interpreter: Interpreter = new Interpreter(r, "", actuators, 0, 0, 0, 0)
            interpreter.run mustBe true
        }
      }

      "evaluate time(\"00:00:00\") >= time(\"00:00:10\") to false" in {
        val script: String = "if(time(\"00:00:00\") >= time(\"00:00:10\"))then alarm; end"
        val actuators = mock[ActuatorService]
        val parser: Parser = new Parser
        parser.parseAll(parser.program, script) match {
          case parser.Success(r: List[Statement], _) =>
            val interpreter: Interpreter = new Interpreter(r, "", actuators, 0, 0, 0, 0)
            interpreter.run mustBe false
        }
      }

      "evaluate time(\"00:00:00\") <= time(\"00:00:00\") to true" in {
          val script: String = "if(time(\"00:00:00\") <= time(\"00:00:00\"))then alarm; end"
          val actuators = mock[ActuatorService]
          val parser: Parser = new Parser
          parser.parseAll(parser.program, script) match {
            case parser.Success(r: List[Statement], _) =>
              val interpreter: Interpreter = new Interpreter(r, "", actuators, 0, 0, 0, 0)
              interpreter.run mustBe true
          }
        }

        "evaluate time(\"00:00:10\") <= time(\"00:00:00\") to false" in {
          val script: String = "if(time(\"00:00:10\") <= time(\"00:00:00\"))then alarm; end"
          val actuators = mock[ActuatorService]
          val parser: Parser = new Parser
          parser.parseAll(parser.program, script) match {
            case parser.Success(r: List[Statement], _) =>
              val interpreter: Interpreter = new Interpreter(r, "", actuators, 0, 0, 0, 0)
              interpreter.run mustBe false
          }
        }

      "evaluate time(\"00:00:00\") != time(\"00:00:10\") to true" in {
          val script: String = "if(time(\"00:00:00\") != time(\"00:00:10\"))then alarm; end"
          val actuators = mock[ActuatorService]
          val parser: Parser = new Parser
          parser.parseAll(parser.program, script) match {
            case parser.Success(r: List[Statement], _) =>
              val interpreter: Interpreter = new Interpreter(r, "", actuators, 0, 0, 0, 0)
              interpreter.run mustBe true
          }
        }

        "evaluate time(\"00:00:00\") != time(\"00:00:00\") to false" in {
          val script: String = "if(time(\"00:00:00\") != time(\"00:00:00\"))then alarm; end"
          val actuators = mock[ActuatorService]
          val parser: Parser = new Parser
          parser.parseAll(parser.program, script) match {
            case parser.Success(r: List[Statement], _) =>
              val interpreter: Interpreter = new Interpreter(r, "", actuators, 0, 0, 0, 0)
              interpreter.run mustBe false
          }
        }
    }
}