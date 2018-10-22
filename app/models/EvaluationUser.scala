package models

import play.api.libs.json.{Json, Writes}

case class EvaluationUser(user: User, activities: Seq[EvaluationActivity]) {

  def id: Long = user.id
  def userId: String = user.userId
  def name: String = user.name

  val point: Int = activities.map(_.point).sum

}

object EvaluationUser {

  import User.format

  implicit val writes: Writes[EvaluationUser] = Writes[EvaluationUser]({
    case e @ EvaluationUser(user, activities) =>
      Json.toJsObject(user) ++ Json.obj(
        "activities" -> activities,
        "point" -> e.point,
      )
  })

}
