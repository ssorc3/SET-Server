package models

import play.api.http._
import play.api.mvc._
import play.api.libs.json._

case class Bridge(bridgeID: String)

case class Device(deviceID: String, userID: String, deviceName: String)
case class DeviceDTO(deviceID: String, deviceName: String)

object Device {
  implicit def writeableSeq(implicit codec: Codec): Writeable[Seq[Device]] = {
    Writeable(data => codec.encode(Json.toJson(data.map(toDTO)).toString))
  }

  implicit def contentTypeSeq(implicit codec: Codec): ContentTypeOf[Seq[Device]] = {
    ContentTypeOf(Some(ContentTypes.TEXT))
  }

  implicit val deviceFormat: OFormat[DeviceDTO] = Json.format[DeviceDTO]

  def toDTO(device: Device): DeviceDTO = {
    DeviceDTO(device.deviceID, device.deviceName)
  }
}