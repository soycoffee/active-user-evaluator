package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.util.Try

case class TypetalkWebhookBody(count: Option[Int], sinceBeforeDays: Option[Int], replyFrom: Long)

object TypetalkWebhookBody {

  def apply(message: String, replyFrom: Long): TypetalkWebhookBody = {
    val pickInt = (key: String) =>
      s"""$key=\\S+""".r.findFirstIn(message).flatMap(Try(_).map(_.split("=")(1).toInt).toOption)
    TypetalkWebhookBody(pickInt("count"), pickInt("sinceBeforeDays"), replyFrom)
  }

  implicit val reads: Reads[TypetalkWebhookBody] = (
    (__ \ "post" \ "message").read[String] and
      (__ \ "post" \ "account" \ "id").read[Long]
    ) (TypetalkWebhookBody(_: String, _: Long))

}
