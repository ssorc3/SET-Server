package controllers

import javax.inject.Inject

import auth.SecuredAuthenticator
import play.api.mvc.{AbstractController, ControllerComponents}
import repositories.DeviceRepository
import websockets.WebSocketManager

import scala.concurrent.ExecutionContext

class ActuatorController @Inject()(cc: ControllerComponents, auth: SecuredAuthenticator, devices: DeviceRepository)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  def setKettleIP = auth.JWTAuthentication.async(parse.json){ implicit request =>
    val userID = request.user.userID
    val kettleIP: String = (request.body \ "ip").asOpt[String].getOrElse("")
    devices.getUserBridges(userID).map{
      _.foreach(b => WebSocketManager.getConnection(b) match{
        case Some(c) => c ! "KETTLEIP:" + kettleIP
      })
    }
  }
}
