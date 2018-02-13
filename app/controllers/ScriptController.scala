package controllers

import javax.inject.Inject

import auth.SecuredAuthenticator
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import repositories.ScriptRepository
import setLang.Parser

import scala.concurrent.{ExecutionContext, Future}

class ScriptController @Inject()(cc: ControllerComponents, auth: SecuredAuthenticator, scripts: ScriptRepository)(implicit ec: ExecutionContext) extends AbstractController(cc){

  def addScript(): Action[JsValue] = auth.JWTAuthentication.async(parse.tolerantJson) { implicit request =>
    val script = (request.body \ "script").asOpt[String].getOrElse("")
    val parser: Parser = new Parser
    parser.parseAll(parser.program, script) match {
      case parser.Success(_, _) =>
        scripts.setUserScript(request.user.userID, script).map(_ => Ok)
      case parser.Error(msg, _) => Future.successful(BadRequest(msg))
      case parser.Failure(msg, _) => Future.successful(BadRequest(msg))
    }
  }

  def removeScript(): Action[JsValue] = auth.JWTAuthentication.async(parse.tolerantJson) { implicit request =>
    scripts.setUserScript(request.user.userID, "").map{ _ =>
      Ok
    }
  }

  def getScript: Action[AnyContent] = auth.JWTAuthentication.async(parse.default){ implicit request =>
    scripts.getUserScript(request.user.userID).map{ s =>
      s.headOption match {
        case Some(script) => Ok(Json.obj("script" -> script))
        case None => NoContent
      }
    }
  }

  def checkScript: Action[JsValue] = auth.JWTAuthentication(parse.json) {implicit request =>
    val script = (request.body \ "script").asOpt[String].getOrElse("")
    val parser: Parser = new Parser
    parser.parseAll(parser.program, script) match {
      case parser.Success(_, _) =>
        Ok
      case parser.Error(msg, _) => BadRequest(msg)
      case _ => Ok("<h1>There's been an error with our compiler. Please try again later</h1>")
    }
  }
}
