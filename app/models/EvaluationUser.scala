package models

import play.api.libs.json.{Json, Writes}

case class EvaluationUser(user: User, activities: Seq[EvaluationActivity], point: Int)

object EvaluationUser {

  import User.format

  implicit val writes: Writes[EvaluationUser] = Writes[EvaluationUser]({
    case EvaluationUser(user, activities, point) =>
      Json.toJsObject(user) ++ Json.obj(
        "activities" -> activities,
        "point" -> point,
      )
  })

}
