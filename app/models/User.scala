package models

import play.api.libs.json.{Json, Reads}

case class User(id: Long, userId: String, name: String)

object User {

  implicit val reads: Reads[User] = Json.reads[User]

}
