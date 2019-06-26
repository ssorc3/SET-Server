package repositories

import javax.inject.Inject

import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class SensorDataRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, devices: DeviceRepository)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]{
  import profile.api._

  class TemperatureDataTable(tag: Tag) extends Table[SensorData](tag, "temperature")
  {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def deviceID = column[String]("deviceID")
    def timestamp = column[Long]("timestamp")
    def value = column[Double]("value")

    def * = (id, deviceID, timestamp, value) <> ((SensorData.apply _).tupled, SensorData.unapply)
  }

  val temperatures = TableQuery[TemperatureDataTable]

  def addTemperatureReading(value: Double, deviceID: String, timestamp: Long): Future[Int] = db.run {
    (temperatures.map(t => (t.value, t.deviceID, t.timestamp))
      returning temperatures.map(_.id)
      into ((stuff, id) => SensorData(id, stuff._2, stuff._3, stuff._1))) += (value, deviceID, timestamp)
  }.map(_.id)

  def getTemperatures(deviceID: String, page: Int): Future[Seq[SensorData]] = db.run {
    temperatures.filter(_.deviceID === deviceID).sortBy(_.timestamp.desc).drop(10*(page-1)).take(25).sortBy(_.timestamp.asc).result
  }

  def getMinuteTemperatures(deviceID: String): Future[Seq[SensorData]] = db.run {
    temperatures.filter(d => d.deviceID === deviceID && d.timestamp > ((System.currentTimeMillis()/1000) - 10*60)).sortBy(_.timestamp.asc).result
  }

  def getHourTemperatures(deviceID: String): Future[Seq[SensorData]] = db.run {
    temperatures.filter(d => d.deviceID === deviceID && d.timestamp > ((System.currentTimeMillis()/1000) - 60*60)).sortBy(_.timestamp.asc).result
  }

  def getDayTemperatures(deviceID: String): Future[Seq[SensorData]] = db.run {
    temperatures.filter(d => d.deviceID === deviceID && d.timestamp > ((System.currentTimeMillis()/1000) - 24*60*60)).sortBy(_.timestamp.asc).result
  }

  def getLatestUserTemperature(userID: String): Future[Seq[Double]] = db.run {
    devices.devices.filter(_.userID === userID).join(temperatures).on(_.deviceID === _.deviceID).sortBy(_._2.timestamp.desc).take(1).map(_._2.value).result
  }

  class HumidityDataTable(tag: Tag) extends Table[SensorData](tag, "humidity")
  {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def deviceID = column[String]("deviceID")
    def timestamp = column[Long]("timestamp")
    def value = column[Double]("value")

    def * = (id, deviceID, timestamp, value) <> ((SensorData.apply _).tupled, SensorData.unapply)
  }

  val humidities = TableQuery[HumidityDataTable]

  def addHumidityReading(value: Double, deviceID: String, timestamp: Long): Future[Int] = db.run {
    (humidities.map(h => (h.value, h.deviceID, h.timestamp))
      returning humidities.map(_.id)
      into ((stuff, id) => SensorData(id, stuff._2, stuff._3, stuff._1))) += (value, deviceID, timestamp)
  }.map(_.id)

  def getHumidity(deviceID: String, page: Int): Future[Seq[SensorData]] = db.run {
    humidities.filter(_.deviceID === deviceID).sortBy(_.timestamp.desc).drop(10*(page-1)).take(25).sortBy(_.timestamp.asc).result
  }

  def getMinuteHumidities(deviceID: String): Future[Seq[SensorData]] = db.run {
    humidities.filter(d => d.deviceID === deviceID && d.timestamp > ((System.currentTimeMillis()/1000) - 10*60)).sortBy(_.timestamp.asc).result
  }

  def getHourHumidities(deviceID: String): Future[Seq[SensorData]] = db.run {
    humidities.filter(d => d.deviceID === deviceID && d.timestamp > ((System.currentTimeMillis()/1000) - 60*60)).sortBy(_.timestamp.asc).result
  }

  def getDayHumidities(deviceID: String): Future[Seq[SensorData]] = db.run {
    humidities.filter(d => d.deviceID === deviceID && d.timestamp > ((System.currentTimeMillis()/1000) - 24*60*60)).sortBy(_.timestamp.asc).result
  }

  def getLatestUserHumidity(userID: String): Future[Seq[Double]] = db.run {
    devices.devices.filter(_.userID === userID).join(humidities).on(_.deviceID === _.deviceID).sortBy(_._2.timestamp.desc).take(1).map(_._2.value).result
  }

  class LightDataTable(tag: Tag) extends Table[SensorData](tag, "light")
  {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def deviceID = column[String]("deviceID")
    def timestamp = column[Long]("timestamp")
    def value = column[Double]("value")

    def * = (id, deviceID, timestamp, value) <> ((SensorData.apply _).tupled, SensorData.unapply)
  }

  val lights = TableQuery[LightDataTable]

  def addLightReading(value: Double, deviceID: String, timestamp: Long): Future[Int] = db.run {
    (lights.map(l => (l.value, l.deviceID, l.timestamp))
      returning lights.map(_.id)
      into ((stuff, id) => SensorData(id, stuff._2, stuff._3, stuff._1))) += (value, deviceID, timestamp)
  }.map(_.id)

  def getLight(deviceID: String, page: Int): Future[Seq[SensorData]] = db.run {
    lights.filter(_.deviceID === deviceID).sortBy(_.timestamp.desc).drop(10*(page-1)).take(25).sortBy(_.timestamp.asc).result
  }

  def getMinuteLights(deviceID: String): Future[Seq[SensorData]] = db.run {
    lights.filter(d => d.deviceID === deviceID && d.timestamp > ((System.currentTimeMillis()/1000) - 10*60)).sortBy(_.timestamp.asc).result
  }

  def getHourLights(deviceID: String): Future[Seq[SensorData]] = db.run {
    lights.filter(d => d.deviceID === deviceID && d.timestamp > ((System.currentTimeMillis()/1000) - 60*60)).sortBy(_.timestamp.asc).result
  }

  def getDayLights(deviceID: String): Future[Seq[SensorData]] = db.run {
    lights.filter(d => d.deviceID === deviceID && d.timestamp > ((System.currentTimeMillis()/1000) - 24*60*60)).sortBy(_.timestamp.asc).result
  }

  def getLatestUserLight(userID: String): Future[Seq[Double]] = db.run {
    devices.devices.filter(_.userID === userID).join(lights).on(_.deviceID === _.deviceID).sortBy(_._2.timestamp.desc).take(1).map(_._2.value).result
  }

  class NoiseDataTable(tag: Tag) extends Table[SensorData](tag, "noise")
  {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def deviceID = column[String]("deviceID")
    def timestamp = column[Long]("timestamp")
    def value = column[Double]("value")

    def * = (id, deviceID, timestamp, value) <> ((SensorData.apply _).tupled, SensorData.unapply)
  }

  val noises = TableQuery[NoiseDataTable]

  def addNoiseReading(value: Double, deviceID: String, timestamp: Long): Future[Int] = db.run{
    (noises.map(n => (n.value, n.deviceID, n.timestamp))
      returning noises.map(_.id)
      into ((stuff, id) => SensorData(id, stuff._2, stuff._3, stuff._1))) += (value, deviceID, timestamp)
  }.map(_.id)

  def getNoise(deviceID: String, page: Int): Future[Seq[SensorData]] = db.run {
    noises.filter(_.deviceID === deviceID).sortBy(_.timestamp.desc).drop(10*(page-1)).take(25).sortBy(_.timestamp.asc).result
  }

  def getMinuteNoise(deviceID: String): Future[Seq[SensorData]] = db.run {
    noises.filter(d => d.deviceID === deviceID && d.timestamp > ((System.currentTimeMillis()/1000) - 10*60)).sortBy(_.timestamp.asc).result
  }

  def getHourNoise(deviceID: String): Future[Seq[SensorData]] = db.run {
    noises.filter(d => d.deviceID === deviceID && d.timestamp > ((System.currentTimeMillis()/1000) - 60*60)).sortBy(_.timestamp.asc).result
  }

  def getDayNoise(deviceID: String): Future[Seq[SensorData]] = db.run {
    noises.filter(d => d.deviceID === deviceID && d.timestamp > ((System.currentTimeMillis()/1000) - 24*60*60)).sortBy(_.timestamp.asc).result
  }

  def getLatestUserNoise(userID: String): Future[Seq[Double]] = db.run {
    devices.devices.filter(_.userID === userID).join(noises).on(_.deviceID === _.deviceID).sortBy(_._2.timestamp.desc).take(1).map(_._2.value).result
  }
}
