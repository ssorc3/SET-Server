package services

import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest}
import repositories.{DeviceRepository, UserRepository}
import scala.setLang.model.PowerSetting.PowerSetting
import websockets.WebSocketManager

import scala.concurrent.ExecutionContext

@Singleton
class ActuatorService @Inject()(devices: DeviceRepository, users: UserRepository, ws: WSClient)(implicit ec: ExecutionContext){
  def sendNotificationToID(userID: String, body: String): Unit =
  {
    users.usernameByID(userID).map{s =>
      sendNotification(s.getOrElse(""), body, "A script send you a notification")
    }
  }

  def sendNotification(username: String, body: String, heading: String): Unit = {
      val request: WSRequest = ws.url("https://onesignal.com/api/v1/notifications").withHttpHeaders(
        "content-type" -> "application/json",
        "Authorization" -> "Basic MWQ1ZWQ4ZGItNGJkNS00ZTNjLTk5NGEtZDRiMzIxNzZiZTlj"
      )

      request.post(Json.obj(
        "app_id" -> "86bc7243-e633-4731-8e0c-b4ec0edbac04",
        "contents" -> Json.obj(
          "en" -> body
        ),
        "headings" -> Json.obj(
          "en" -> heading
        ),
        "included_segments" -> Json.arr(username)
      ))
  }

  def sendGlobalNotification(body: String, heading: String): Unit = {
    val request: WSRequest = ws.url("https://onesignal.com/api/v1/notifications").withHttpHeaders(
      "content-type" -> "application/json",
      "Authorization" -> "Basic MWQ1ZWQ4ZGItNGJkNS00ZTNjLTk5NGEtZDRiMzIxNzZiZTlj"
    )

    request.post(Json.obj(
      "app_id" -> "86bc7243-e633-4731-8e0c-b4ec0edbac04",
      "contents" -> Json.obj(
          "en" -> body
        ),
        "headings" -> Json.obj(
          "en" -> heading
        ),
      "included_segments" -> Json.arr("All")
    ))
  }

  def changeKettlePowerSetting(userID: String, command: PowerSetting): Unit = {
    sendToUserBridge(userID, "kettle " + command)
  }

  def setLightSetting(userID: String, zone: Int, isWhite: Boolean, hue: Int, brightness: Int): Unit = {
    sendToUserBridge(userID, "lightSetting " + zone + " " + isWhite  + " " + hue + " " + brightness)
  }

  def changeLightPowerSetting(userID: String, command: PowerSetting): Unit = {
    sendToUserBridge(userID, "lights " + command)
  }

  def changePlugPowerSetting(userID: String, command: PowerSetting): Unit = {
    sendToUserBridge(userID, "plug " + command)
  }

  def activateAlarm(userID: String, command: PowerSetting): Unit = {
    sendToUserBridge(userID, "alert " + command)
  }

  def armAlarm(userID: String, command: PowerSetting): Unit = {
    sendToUserBridge(userID, "alarmarmed " + command)
  }

  def sendToUserBridge(userID: String, message: String): Unit =
  {
    devices.getUserBridges(userID).map(_.foreach(b => {
      WebSocketManager.getConnection(b) match {
        case Some(c) => c ! message
        case None =>
      }
    }))
  }
}
