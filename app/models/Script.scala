package models

import play.api.http.{ContentTypeOf, ContentTypes, Writeable}
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.Codec

case class Script(userID: String, scriptName: String, script: String, lastRun: Long)

case class ScriptDTO(scriptName: String, script: String)

object Script{
  implicit def writeableSeq(implicit codec: Codec): Writeable[Seq[Script]] = {
    Writeable(data => codec.encode(Json.toJson(data.map(toDTO)).toString))
  }

  implicit def contentTypeSeq(implicit codec: Codec): ContentTypeOf[Seq[Script]] = {
    ContentTypeOf(Some(ContentTypes.TEXT))
  }

  implicit val scriptFormat: OFormat[ScriptDTO] = Json.format[ScriptDTO]

  def toDTO(script: Script): ScriptDTO = {
    ScriptDTO(script.scriptName, script.script)
  }
}