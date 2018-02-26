package controllers

import javax.inject.Inject

import auth.SecuredAuthenticator
import play.api.libs.json.JsValue
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import repositories.{DeviceRepository, UserRepository}
import services.ActuatorService
import setLang.model.PowerSetting

import scala.concurrent.ExecutionContext

class ActuatorController @Inject()(cc: ControllerComponents, auth: SecuredAuthenticator, devices: DeviceRepository, users: UserRepository, actuators: ActuatorService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def boilKettle(): Action[AnyContent] = auth.JWTAuthentication(parse.default) { implicit request =>
    val userID = request.user.userID
    actuators.changeKettlePowerSetting(userID, PowerSetting.ON)
    Ok
  }

  def setLights(): Action[JsValue] = auth.JWTAuthentication(parse.json) {implicit request =>
    val userID = request.user.userID
    val isWhite: Boolean = (request.body \ "isWhite").asOpt[Boolean].getOrElse(false)
    val hue: Int = (request.body \ "hue").asOpt[Int].getOrElse(255)
    val brightness: Int = (request.body \ "brightness").asOpt[Int].getOrElse(255)
    actuators.setLightSetting(userID, isWhite, hue, brightness)
    Ok
  }

  def setPlug(): Action[JsValue] = auth.JWTAuthentication(parse.json) { implicit request =>
    val userID = request.user.userID
    val on = (request.body \ "on").asOpt[Boolean].getOrElse(false)
    actuators.changePlugPowerSetting(userID, if(on) PowerSetting.ON else PowerSetting.OFF)
    Ok
  }
}
