package models.typetalk

import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.util.Try

case class WebhookRequestBody(count: Option[Int], sinceBeforeDays: Option[Int], replyFrom: Long)

object WebhookRequestBody {

  def apply(message: String, replyFrom: Long): WebhookRequestBody = {
    val pickInt = (key: String) =>
      s"""$key=\\S+""".r.findFirstIn(message).flatMap(Try(_).map(_.split("=")(1).toInt).toOption)
    WebhookRequestBody(pickInt("count"), pickInt("sinceBeforeDays"), replyFrom)
  }

  implicit val reads: Reads[WebhookRequestBody] = (
    (__ \ "post" \ "message").read[String] and
      (__ \ "post" \ "account" \ "id").read[Long]
    ) (WebhookRequestBody(_: String, _: Long))

}
