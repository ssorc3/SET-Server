package controllers

import javax.inject.Inject

import auth.SecuredAuthenticator
import play.api.libs.json.JsValue
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import repositories.ZoneRepository

import scala.concurrent.{ExecutionContext, Future}

class ZoneController @Inject()(cc: ControllerComponents, auth: SecuredAuthenticator, zones: ZoneRepository)(ec: ExecutionContext) extends AbstractController(cc)
{
  def createZone(): Action[JsValue] = auth.JWTAuthentication.async(parse.json) { implicit request =>
    val userID = request.user.userID
    val zoneName = (request.body \ "zoneName").asOpt[String].getOrElse("default")
    zones.exists(userID, zoneName).flatMap{
      case false =>
        zones.create(userID, zoneName).map(_ => Ok("Zone " + zoneName + " created"))
      case true => Future.successful(BadRequest("Zone with the name " + zoneName + " already exists"))
    }
  }

  def rename() = auth.JWTAuthentication.async(parse.json) {implicit request =>
    val userID = request.user.userID
    val currentName = (request.body \ "current").as[String]
    val newName = (request.body \ "new").as[String]

    zones.getID(userID, currentName).map{z =>
      z.foreach(zones.rename(_, userID, newName).map(_ => Ok))
    }
  }

  
}
