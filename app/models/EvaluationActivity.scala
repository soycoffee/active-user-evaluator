package models

import java.time.LocalDateTime

import play.api.libs.json.{JsObject, Json, Writes}

case class EvaluationActivity(activity: Activity, point: Int) {

  def `type`: Activity.Type = activity.`type`
  def created: LocalDateTime = activity.created
  def content: JsObject = activity.content

}

object EvaluationActivity {

  implicit val writes: Writes[EvaluationActivity] = Writes[EvaluationActivity]({
    case EvaluationActivity(activity, point) =>
      Json.toJsObject(activity) - "content" ++ Json.obj(
        "point" -> point,
      )
  })

}
