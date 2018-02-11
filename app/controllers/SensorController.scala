package controllers

import javax.inject.Inject

import auth.SecuredAuthenticator
import com.google.inject.Singleton
import play.api.libs.json.JsValue
import play.api.mvc._
import repositories.{DeviceRepository, ScriptRepository, SensorDataRepository}
import setLang.model.Statement
import setLang.{Interpreter, Parser}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class SensorController @Inject()(cc: MessagesControllerComponents, auth: SecuredAuthenticator, devices: DeviceRepository, sensors: SensorDataRepository, scripts: ScriptRepository)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){

  def registerDevice(deviceID: String): Action[JsValue] = auth.JWTAuthentication.async(parse.json) { implicit request =>
    val userID = request.user.userID
    val deviceName = (request.body \ "deviceName").as[String]
    devices.exists(deviceID).flatMap{
      case false =>
        devices.create(deviceID, userID, deviceName).map(_ => Ok("Device Registered"))
      case true => Future.successful(BadRequest("Device already registered"))
    }
  }

  def getUserDevices: Action[AnyContent] = auth.JWTAuthentication.async(parse.anyContent) { implicit request =>
    val userID = request.user.userID
    devices.getUserDevices(userID).map(d => Ok(d))
  }

  def deleteUserDevice(deviceID: String): Action[AnyContent] = auth.JWTAuthentication.async(parse.anyContent) { implicit request =>
    val userID = request.user.userID
    devices.delete(deviceID, userID).map{
      case true =>
        Ok("Device has been deleted")
      case false =>
        BadRequest("Either the device doesn't exist or the user provided does not own the device")
    }
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

  def getTemperature(deviceID: String, page: Int): Action[JsValue] = auth.JWTAuthentication.async(parse.tolerantJson) { implicit request =>
    devices.deviceBelongsToUser(deviceID, request.user.userID).flatMap{
      case true =>
        sensors.getTemperatures(deviceID, page).map(t => Ok(t))
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

  def getHumidity(deviceID: String, page: Int): Action[JsValue] = auth.JWTAuthentication.async(parse.tolerantJson) { implicit request =>
    devices.deviceBelongsToUser(deviceID, request.user.userID).flatMap{
      case true =>
        sensors.getHumidity(deviceID, page).map(t => Ok(t))
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

  def getLight(deviceID: String, page: Int): Action[JsValue] = auth.JWTAuthentication.async(parse.tolerantJson) { implicit request =>
    devices.deviceBelongsToUser(deviceID, request.user.userID).flatMap{
      case true =>
        sensors.getLight(deviceID, page).map(t => Ok(t))
      case false => Future.successful(BadRequest("Invalid device"))
    }
  }

  def receiveNoise(deviceID: String): Action[JsValue] = Action.async(parse.tolerantJson) { implicit request =>
    devices.exists(deviceID).flatMap{
      case true =>
        val value: Int = (request.body \ "value").as[Int]
        val timestamp: Long = (request.body \ "timestamp").as[Long]
        sensors.addNoiseReading(value, deviceID, timestamp).map(_ => Ok("Noise recorded"))
      case false => Future.successful(BadRequest("Invalid device"))
    }
  }

  def getNoise(deviceID: String, page: Int): Action[JsValue] = auth.JWTAuthentication.async(parse.tolerantJson) { implicit request =>
    devices.deviceBelongsToUser(deviceID, request.user.userID).flatMap{
      case true =>
        sensors.getNoise(deviceID, page).map(t => Ok(t))
      case false => Future.successful(BadRequest("Invalid Device"))
    }
  }
}
