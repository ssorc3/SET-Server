package controllers

import javax.inject.Inject

import auth.SecuredAuthenticator
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import repositories.ScriptRepository
import scala.setLang.Parser

import scala.concurrent.{ExecutionContext, Future}

class ScriptController @Inject()(cc: ControllerComponents, auth: SecuredAuthenticator, scripts: ScriptRepository)(implicit ec: ExecutionContext) extends AbstractController(cc){

  def addScript(): Action[JsValue] = auth.JWTAuthentication.async(parse.tolerantJson) { implicit request =>
    val script = (request.body \ "script").asOpt[String].getOrElse("")
    val scriptName = (request.body \ "scriptName").asOpt[String].getOrElse("default")
    val parser: Parser = new Parser
    parser.parseAll(parser.program, script) match {
      case parser.Success(_, _) =>
        scripts.setUserScript(request.user.userID, scriptName, script).map(_ => Ok)
      case parser.Error(msg, _) => Future.successful(BadRequest(msg))
      case parser.Failure(msg, _) => Future.successful(BadRequest(msg))
    }
  }

  def removeScript(): Action[JsValue] = auth.JWTAuthentication.async(parse.tolerantJson) { implicit request =>
    val scriptName = (request.body \ "script").asOpt[String].getOrElse("default")
    scripts.deleteUserScript(request.user.userID, scriptName).map{ _ =>
      Ok
    }
  }

  def getScripts: Action[AnyContent] = auth.JWTAuthentication.async(parse.default) { implicit request =>
    scripts.getUserScripts(request.user.userID).map{ s =>
      Ok(s)
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

  def getAdminScripts(): Action[AnyContent] = auth.JWTAuthentication.async(parse.default) { implicit request =>
    scripts.getUserScripts("Admin").map(s => Ok(s))
  }
}
