package services

import javax.inject.{Inject, Singleton}

import repositories.DeviceRepository
import setLang.model.PowerSetting.PowerSetting
import websockets.WebSocketManager

import scala.concurrent.ExecutionContext

@Singleton
class ActuatorService @Inject() (devices: DeviceRepository)(implicit ec: ExecutionContext){
  def changeKettlePowerSetting(userID: String, command: PowerSetting): Unit = {
    sendToUserBridge(userID, "kettle " + command)
  }

  def setLightSetting(userID: String, isWhite: Boolean, hue: Int, brightness: Int): Unit = {
    sendToUserBridge(userID, "lightSetting " + isWhite  + " " + hue + " " + brightness)
  }

  def changeLightPowerSetting(userID: String, command: PowerSetting): Unit = {
    sendToUserBridge(userID, "lights " + command)
  }

  def changePlugPowerSetting(userID: String, command: PowerSetting): Unit = {
    sendToUserBridge(userID, "plug " + command)
  }

  def activateAlarm(userID: String): Unit = {
    sendToUserBridge(userID, "alert")
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
