package models

import java.time.LocalDateTime

import play.api.libs.json._

case class EvaluationActivity(activity: Activity, point: Int) {

  def `type`: Activity.Type = activity.`type`
  def created: LocalDateTime = activity.created
  def content: JsObject = activity.content

}

object EvaluationActivity {

  implicit val writes: Writes[EvaluationActivity] = Writes[EvaluationActivity]({
    case EvaluationActivity(Activity(_type, _, created, _), point) =>
      Json.obj(
        "type" -> _type.toString,
        "created" -> created,
        "point" -> point,
      )
  })

}
