package models

import play.api.http.{ContentTypeOf, ContentTypes, Writeable}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.Codec

case class SensorData(id: Int, deviceID: String, timestamp: Long, value: Double)

object SensorData {
  implicit def writeableSeq(implicit codec: Codec): Writeable[Seq[SensorData]] = {
    Writeable(data => codec.encode(Json.toJson(data).toString))
  }

  implicit def contentTypeSeq(implicit codec: Codec): ContentTypeOf[Seq[SensorData]] = {
    ContentTypeOf(Some(ContentTypes.TEXT))
  }

  implicit val deviceFormat: OFormat[SensorData] = Json.format[SensorData]
}