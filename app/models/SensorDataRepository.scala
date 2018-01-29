package models

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class SensorDataRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]{
  import profile.api._

  class TemperatureDataTable(tag: Tag) extends Table[TemperatureData](tag, "temperature")
  {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def deviceID = column[String]("deviceID")
    def timestamp = column[Long]("timestamp")
    def value = column[Double]("value")

    def * = (id, deviceID, timestamp, value) <> ((TemperatureData.apply _).tupled, TemperatureData.unapply)
  }

  val temperatures = TableQuery[TemperatureDataTable]

  def addTemperatureReading(value: Double, deviceID: String, timestamp: Long): Future[Int] = db.run {
    (temperatures.map(t => (t.value, t.deviceID, t.timestamp))
      returning temperatures.map(_.id)
      into ((stuff, id) => TemperatureData(id, stuff._2, stuff._3, stuff._1))) += (value, deviceID, timestamp)
  }.map(_.id)

  class HumidityDataTable(tag: Tag) extends Table[HumidityData](tag, "humidity")
  {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def deviceID = column[String]("deviceID")
    def timestamp = column[Long]("timestamp")
    def value = column[Double]("value")

    def * = (id, deviceID, timestamp, value) <> ((HumidityData.apply _).tupled, HumidityData.unapply)
  }

  val humidities = TableQuery[HumidityDataTable]

  def addHumidityReading(value: Double, deviceID: String, timestamp: Long): Future[Int] = db.run {
    (humidities.map(h => (h.value, h.deviceID, h.timestamp))
      returning humidities.map(_.id)
      into ((stuff, id) => HumidityData(id, stuff._2, stuff._3, stuff._1))) += (value, deviceID, timestamp)
  }.map(_.id)

  class LightDataTable(tag: Tag) extends Table[LightData](tag, "light")
  {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def deviceID = column[String]("deviceID")
    def timestamp = column[Long]("timestamp")
    def value = column[Double]("value")

    def * = (id, deviceID, timestamp, value) <> ((LightData.apply _).tupled, LightData.unapply)
  }

  val lights = TableQuery[LightDataTable]

  def addLightReading(value: Double, deviceID: String, timestamp: Long): Future[Int] = db.run {
    (lights.map(l => (l.value, l.deviceID, l.timestamp))
      returning lights.map(_.id)
      into ((stuff, id) => LightData(id, stuff._2, stuff._3, stuff._1))) += (value, deviceID, timestamp)
  }.map(_.id)

  def delete(): Future[Int] = db.run {
    temperatures.delete
    humidities.delete
    lights.delete
  }
}
