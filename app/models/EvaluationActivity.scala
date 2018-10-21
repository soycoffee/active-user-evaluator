package models

import play.api.libs.json.{Json, Writes}

case class EvaluationActivity(activity: Activity, point: Int)

object EvaluationActivity {

  implicit val writes: Writes[EvaluationActivity] = Writes[EvaluationActivity]({
    case EvaluationActivity(activity, point) =>
      Json.toJsObject(activity) - "content" ++ Json.obj(
        "point" -> point,
      )
  })

}
