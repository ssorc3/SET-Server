package controllers

import javax.inject.Inject

import auth.SecuredAuthenticator
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSClient, WSRequest}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import repositories.ZoneRepository

import scala.concurrent.{ExecutionContext, Future}

class ZoneController @Inject()(cc: ControllerComponents, auth: SecuredAuthenticator, zones: ZoneRepository, ws: WSClient)(implicit ec: ExecutionContext) extends AbstractController(cc)
{
  def createZone(): Action[JsValue] = auth.JWTAuthentication.async(parse.json) { implicit request =>
    val userID = request.user.userID
    val zoneName = (request.body \ "zoneName").asOpt[String].getOrElse("default").toLowerCase
    zones.exists(userID, zoneName).flatMap{
      case false =>
        zones.create(userID, zoneName).map(_ => Ok("Zone " + zoneName + " created"))
      case true => Future.successful(BadRequest("Zone with the name " + zoneName + " already exists"))
    }
  }

  def renameZone(): Action[JsValue] = auth.JWTAuthentication.async(parse.json) { implicit request =>
    val userID = request.user.userID
    val currentName = (request.body \ "current").as[String]
    val newName = (request.body \ "new").as[String]

    zones.getID(userID, currentName).flatMap{z =>
      z.headOption match {
        case Some(x) => zones.rename(x, userID, newName).map(_ => Ok)
        case None => Future.successful(BadRequest("Zone does not exist"))
      }
    }
  }

  def deleteZone(): Action[JsValue] = auth.JWTAuthentication.async(parse.json) {implicit request =>
    val userID = request.user.userID
    val zoneName = (request.body \ "zoneName").as[String].toLowerCase
    val url: WSRequest = ws.url("http://sccug-330-03.lancs.ac.uk:8000/location?group=hopefulhyena&locations=zoneName")
    val response = url.delete()
    response.flatMap{ r =>
      if((r.json \ "success").as[Boolean])
      {
        zones.getID(userID, zoneName).flatMap{ z =>
          z.headOption match {
            case Some(x) => zones.delete(x, userID).map(_ => Ok)
            case None => Future.successful(BadRequest("Zone does not exist"))
          }
        }
      }
      else
      {
         Future.successful(InternalServerError("Could not delete zone from Find, please try again"))
      }
    }
  }

  def getZones: Action[AnyContent] = auth.JWTAuthentication.async(parse.default) { implicit request =>
    val userID = request.user.userID
    zones.getZones(userID).map(z => Ok(z))
  }


}
