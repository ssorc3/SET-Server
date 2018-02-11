package controllers

import javax.inject.Inject

import auth.SecuredAuthenticator
import play.api.libs.json.JsValue
import play.api.mvc.{AbstractController, Action, ControllerComponents}
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
}
