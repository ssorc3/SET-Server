package models

import javax.inject.Inject

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

    def * = (deviceID, userID, deviceName) <> ((Device.apply _).tupled, Device.unapply)
  }

  val devices = TableQuery[DeviceTable]

  def create(deviceID: String, userID: String, deviceName: String): Future[Any] = db.run{
    devices += Device(deviceID, userID, deviceName)
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

  def delete(): Future[Int] = db.run{
    devices.delete
  }
}
