package repositories

import javax.inject.Inject

import models.Zone
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class ZoneRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]
{
  import profile.api._

  class ZoneTable(tag: Tag) extends Table[Zone](tag, "zones")
  {
    def id = column[Int]("zoneID", O.PrimaryKey, O.AutoInc)
    def userID = column[String]("userID")
    def zoneName = column[String]("deviceID")

    def * = (id, userID, zoneName) <> ((Zone.apply _).tupled, Zone.unapply)
  }

  val zones = TableQuery[ZoneTable]

  def create(userID: String, zoneName: String): Future[Int] = db.run {
    (zones.map(z => (z.userID, z.zoneName))
      returning zones.map(_.id)
      into ((stuff, id) => Zone(id, stuff._1, stuff._2)) += (userID, zoneName))
  }.map(_.id)

  def getName(zoneID: Int): Future[String] = db.run {
    zones.filter(_.id === zoneID).distinct.map(_.zoneName).result
  }.map(_.head)

  def exists(userID: String, zoneName: String): Future[Boolean] = db.run {
    zones.filter(z => z.userID === userID && z.zoneName === zoneName).exists.result
  }

  def getID(userID: String, zoneName: String): Future[Seq[Int]] = db.run {
    zones.filter(z => z.userID === userID && z.zoneName === zoneName).map(_.id).result
  }

  def rename(zoneID: Int, userID: String, zoneName: String): Future[Any] = db.run {
    zones.filter(z => z.id === zoneID && z.userID === userID).map(_.zoneName).update(zoneName)
  }

  def delete(zoneID: Int, userID: String): Future[Any] = db.run {
    zones.filter(z => z.id === zoneID && z.userID === userID).delete
  }

  def getZones(userID: String): Future[Seq[Zone]] = db.run {
    zones.filter(_.userID === userID).sortBy(_.zoneName.asc).result
  }
}
