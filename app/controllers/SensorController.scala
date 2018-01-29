package controllers

import javax.inject.Inject

import auth.SecuredAuthenicator
import com.google.inject.Singleton
import models.{DeviceRepository, SensorDataRepository}
import play.api.libs.json.JsValue
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SensorController @Inject()(cc: MessagesControllerComponents, auth: SecuredAuthenicator, devices: DeviceRepository, sensors: SensorDataRepository)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){

  def registerDevice(): Action[JsValue] = auth.JWTAuthentication.async(parse.json) { implicit request =>
    val deviceID = (request.body \ "deviceID").as[String]
    val userID = request.user.userID
    val deviceName = (request.body \ "deviceName").as[String]
    devices.create(deviceID, userID, deviceName).map(_ => Ok("Device Registered"))
  }

  def getUserDevices(): Action[AnyContent] = auth.JWTAuthentication.async(parse.anyContent) { implicit request =>
    val userID = request.user.userID
    devices.getUserDevices(userID).map(d => Ok(d))
  }

  // /sensor/{type}/{deviceID}
  def receiveTemperature(deviceID: String): Action[JsValue] = Action.async(parse.tolerantJson) { implicit request =>
    devices.exists(deviceID).flatMap{
      case true =>
        val value: Double = (request.body \ "value").as[Double]
        val timestamp: Long = (request.body \ "timestamp").as[Long]
        sensors.addTemperatureReading(value, deviceID, timestamp).map(_ => Ok("Temperature recorded"))
      case false => Future.successful(BadRequest("Invalid device"))
    }
  }

  def receiveHumidity(deviceID: String): Action[JsValue] = Action.async(parse.tolerantJson) { implicit request =>
    devices.exists(deviceID).flatMap{
      case true =>
        val value: Double = (request.body \ "value").as[Double]
        val timestamp: Long = (request.body \ "timestamp").as[Long]
        sensors.addHumidityReading(value, deviceID, timestamp).map(_ => Ok("Humidity recorded"))
      case false => Future.successful(BadRequest("Invalid device"))
    }
  }

  def receiveLight(deviceID: String): Action[JsValue] = Action.async(parse.tolerantJson) { implicit request =>
    devices.exists(deviceID).flatMap{
      case true =>
        val value: Double = (request.body \ "value").as[Double]
        val timestamp: Long = (request.body \ "timestamp").as[Long]
        sensors.addLightReading(value, deviceID, timestamp).map(_ => Ok("Light recorded"))
      case false => Future.successful(BadRequest("Invalid device"))
    }
  }
}
