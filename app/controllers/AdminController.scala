package controllers

import javax.inject.{Inject, Singleton}

import auth.SecuredAuthenticator
import play.api.libs.json.JsValue
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import repositories.{DeviceRepository, ScriptRepository, UserRepository}
import services.ActuatorService

import scala.concurrent.{ExecutionContext, Future}
import scala.setLang.Parser

@Singleton
class AdminController @Inject()(cc: ControllerComponents, auth: SecuredAuthenticator, scripts: ScriptRepository,
                                users: UserRepository, actuators: ActuatorService,
                                devices: DeviceRepository)(implicit ec: ExecutionContext) extends AbstractController(cc)
{
  def setAdminScript(): Action[JsValue] = auth.AdminAuthentication.async(parse.json) {implicit request =>
    val script = (request.body \ "script").asOpt[String].getOrElse("")
    val scriptName = (request.body \ "scriptName").asOpt[String].getOrElse("default")
    val parser: Parser = new Parser
    parser.parseAll(parser.program, script) match {
      case parser.Success(_, _) =>
        scripts.setUserScript("Admin", scriptName, script).map(_ => Ok)
      case parser.Error(msg, _) => Future.successful(BadRequest(msg))
      case parser.Failure(msg, _) => Future.successful(BadRequest(msg))
    }
  }

  def deleteAdminScript(): Action[JsValue] = auth.AdminAuthentication(parse.json) { implicit request =>
    val scriptName = (request.body \ "scriptName").asOpt[String] match {
      case Some(s) => scripts.deleteUserScript("Admin", scriptName)
      case None =>
    }
    Ok
  }

  def getAllUsers(): Action[AnyContent] = auth.AdminAuthentication.async(parse.default) {implicit request =>
    users.getAllUsers().map(u => Ok(u))
  }

  def deleteUser(): Action[JsValue] = auth.AdminAuthentication.async(parse.json) { implicit request =>
    (request.body \ "username").asOpt[String].fold{
      Future.successful(BadRequest("Please specify a username"))
    }
    { username =>
      users.deleteUser(username)
      Future.successful(Ok("Deleted user " + username))
    }
  }

  def getAllDevices(): Action[AnyContent] = auth.AdminAuthentication.async(parse.default) { implicit request =>
    devices.getAllDevices().map(d => Ok(d))
  }

  def sendNotification(): Action[JsValue] = auth.AdminAuthentication(parse.json) { implicit request =>
    val body = (request.body \ "body").asOpt[String].getOrElse("")
    val heading = (request.body \ "title").asOpt[String].getOrElse("SETApp")
    val username = (request.body \ "username").asOpt[String]

    username match {
      case Some(u) => actuators.sendNotification(u, body, heading)
      case None => actuators.sendGlobalNotification(body, heading)
    }
    Ok
  }

  def Cors() = Action(parse.default){
    Ok
  }
}
