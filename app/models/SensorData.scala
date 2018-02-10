package models

import play.api.http.{ContentTypeOf, ContentTypes, Writeable}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.Codec

case class TemperatureData(id: Int, deviceID: String, timestamp: Long, value: Double)

object TemperatureData {
  implicit def writeableSeq(implicit codec: Codec): Writeable[Seq[TemperatureData]] = {
    Writeable(data => codec.encode(Json.toJson(data).toString))
  }

  implicit def contentTypeSeq(implicit codec: Codec): ContentTypeOf[Seq[TemperatureData]] = {
    ContentTypeOf(Some(ContentTypes.TEXT))
  }

  implicit val deviceFormat: OFormat[TemperatureData] = Json.format[TemperatureData]
}

case class HumidityData(id: Int, deviceID: String, timestamp: Long, value: Double)

object HumidityData {
  implicit def writeableSeq(implicit codec: Codec): Writeable[Seq[HumidityData]] = {
    Writeable(data => codec.encode(Json.toJson(data).toString))
  }

  implicit def contentTypeSeq(implicit codec: Codec): ContentTypeOf[Seq[HumidityData]] = {
    ContentTypeOf(Some(ContentTypes.TEXT))
  }

  implicit val deviceFormat: OFormat[HumidityData] = Json.format[HumidityData]
}

case class LightData(id: Int, deviceID: String, timestamp: Long, value: Double)

object LightData {
  implicit def writeableSeq(implicit codec: Codec): Writeable[Seq[LightData]] = {
    Writeable(data => codec.encode(Json.toJson(data).toString))
  }

  implicit def contentTypeSeq(implicit codec: Codec): ContentTypeOf[Seq[LightData]] = {
    ContentTypeOf(Some(ContentTypes.TEXT))
  }

  implicit val deviceFormat: OFormat[LightData] = Json.format[LightData]
}

case class NoiseData(id: Int, deviceID: String, timestamp: Long, value: Int)

object NoiseData {
  implicit def writeableSeq(implicit codec: Codec): Writeable[Seq[NoiseData]] = {
    Writeable(data => codec.encode(Json.toJson(data).toString))
  }

  implicit def contentTypeSeq(implicit codec: Codec): ContentTypeOf[Seq[NoiseData]] = {
    ContentTypeOf(Some(ContentTypes.TEXT))
  }

  implicit val deviceFormat: OFormat[NoiseData] = Json.format[NoiseData]
}