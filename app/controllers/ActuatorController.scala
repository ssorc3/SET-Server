package controllers

import javax.inject.Inject

import auth.SecuredAuthenticator
import play.api.libs.json.JsValue
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import repositories.DeviceRepository
import websockets.WebSocketManager

import scala.concurrent.ExecutionContext

class ActuatorController @Inject()(cc: ControllerComponents, auth: SecuredAuthenticator, devices: DeviceRepository)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  def setKettleIP(): Action[JsValue] = auth.JWTAuthentication.async(parse.json){ implicit request =>
    val userID = request.user.userID
    val kettleIP: String = (request.body \ "ip").asOpt[String].getOrElse("")
    devices.getUserBridges(userID).map{x =>
      x.foreach(b => WebSocketManager.getConnection(b) match{
        case Some(c) => c ! "KETTLEIP:" + kettleIP
      })
      Ok
    }
  }

  def setLightIP(): Action[JsValue] = auth.JWTAuthentication.async(parse.json){ implicit request =>
    val userID = request.user.userID
    val lightIP: String = (request.body \ "ip").asOpt[String].getOrElse("")
    val lightMac: String = (request.body \ "mac").asOpt[String].getOrElse("")
    devices.getUserBridges(userID).map{x =>
      x.foreach(b => WebSocketManager.getConnection(b) match{
        case Some(c) =>
          c ! "LIGHTIP:" + lightIP
          c ! "LIGHTMAC:" + lightMac
      })
      Ok
    }
  }
}
