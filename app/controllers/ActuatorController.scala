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
        case _ =>
      })
      Ok
    }
  }

  def boilKettle(): Action[JsValue] = auth.JWTAuthentication.async(parse.json) {implicit request =>
    val userID = request.user.userID
    devices.getUserBridges(userID).map {x =>
      x.foreach(b => WebSocketManager.getConnection(b) match {
        case Some(c) =>
          c ! "kettle"
        case _ =>
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
          c ! "LIGHTIP: " + lightIP + " " + lightMac
        case _ =>
      })
      Ok
    }
  }

  def setLights(): Action[JsValue] = auth.JWTAuthentication.async(parse.json) {implicit request =>
    val userID = request.user.userID
    val isWhite: Boolean = (request.body \ "isWhite").asOpt[Boolean].getOrElse(false)
    val hue: Int = (request.body \ "hue").asOpt[Int].getOrElse(255)
    val brightness: Int = (request.body \ "brightness").asOpt[Int].getOrElse(255)
    devices.getUserBridges(userID).map {x =>
      x.foreach(b => WebSocketManager.getConnection(b) match {
        case Some(c) =>
          c ! "lightSetting " + isWhite + " " + hue  + " " + brightness
        case _ =>
      })
      Ok
    }
  }
}
