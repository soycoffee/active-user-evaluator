package models

import play.api.libs.json.{Json, OFormat}

case class User(id: Long, userId: String, name: String)

object User {

  implicit val format: OFormat[User] = Json.format[User]

}
