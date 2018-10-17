package models

import play.api.libs.json.{Json, OFormat}

case class ApiDefinition(key: String, backlogDomain: String, backlogApiKey: String)

object ApiDefinition {

  implicit val format: OFormat[ApiDefinition] = Json.format[ApiDefinition]

}
