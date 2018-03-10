package repositories

import javax.inject.Inject

import models.{Bridge, Device}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class DeviceRepository @Inject()(protected val dbConfigProvider:DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]{
  import profile.api._

  class DeviceTable(tag: Tag) extends Table[Device](tag, "devices")
  {
    def deviceID = column[String]("deviceID", O.PrimaryKey)
    def userID = column[String]("userID")
    def deviceName = column[String]("deviceName")
    def zoneID = column[Int]("zoneID")

    def * = (deviceID, userID, deviceName, zoneID) <> ((Device.apply _).tupled, Device.unapply)
  }

  val devices = TableQuery[DeviceTable]

  def create(deviceID: String, userID: String, deviceName: String): Future[Any] = db.run{
    devices += Device(deviceID, userID, deviceName, -1)
  }

  def assignZone(deviceID: String, userID: String, zoneID: Int): Future[Any] = db.run {
    devices.filter(d => d.deviceID === deviceID && d.userID === userID).map(_.zoneID).update(zoneID)
  }

  def unassignZone(deviceID: String, userID: String): Future[Any] = db.run {
    devices.filter(d => d.deviceID === deviceID && d.userID === userID).map(_.zoneID).update(null)
  }

  def getDevicesInZone(userID: String, zoneID: Int): Future[Seq[Device]] = db.run {
    devices.filter(d => d.userID === userID && d.zoneID === zoneID).result
  }

  def rename(deviceID: String, userID: String, deviceName: String): Future[Any] = db.run{
    devices.filter(d => d.deviceID === deviceID && d.userID === userID).map(_.deviceName).update(deviceName)
  }

  def getUserDevices(userID: String): Future[Seq[Device]] = db.run {
    devices.filter(_.userID === userID).result
  }

  def exists(deviceID: String): Future[Boolean] = db.run {
    devices.filter(_.deviceID === deviceID).exists.result
  }

  def deviceBelongsToUser(deviceID: String, userID: String): Future[Boolean] = db.run{
    devices.filter(d => d.deviceID === deviceID && d.userID === userID).exists.result
  }

  def delete(deviceID: String, userID: String): Future[Boolean] = {
    val device = devices.filter(d => d.userID === userID && d.deviceID === deviceID)
    db.run(device.exists.result).flatMap {
      case true => db.run{
        deleteBridges(deviceID, userID)
        device.delete
      }.map(_ => true)
      case false => Future(false)
    }
  }

  class BridgeTable(tag: Tag) extends Table[Bridge](tag, "bridges")
  {
    def deviceID = column[String]("bridgeID", O.PrimaryKey)

    def * = deviceID <> (Bridge.apply, Bridge.unapply)
  }

  val bridges = TableQuery[BridgeTable]

  def setAsBridge(deviceID: String): Future[Any] = db.run {
    bridges += Bridge(deviceID)
  }

  def deleteBridges(deviceID: String, userID: String): Future[Any] = db.run{
    bridges.filter(b => b.deviceID === deviceID).delete
  }

  def getUserBridges(userID: String): Future[Seq[String]] = {
    val ids = for {
      device <- devices if device.userID === userID
      bridge <- bridges if device.deviceID === bridge.deviceID
    } yield bridge.deviceID
    db.run(ids.result)
  }

  def removeBridge(deviceID: String): Future[Any] = db.run {
    bridges.filter(_.deviceID === deviceID).delete
  }

  def isBridge(deviceID: String): Future[Boolean] = db.run{
    bridges.filter(_.deviceID === deviceID).exists.result
  }
}
