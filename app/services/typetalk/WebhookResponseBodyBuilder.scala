package services.typetalk

import javax.inject.Singleton
import models._
import play.api.libs.json.{JsObject, Json}

import scala.Function.tupled

@Singleton
class WebhookResponseBodyBuilder {

  private val Separation = "----------------"

  def apply(domain: String, evaluationUsers: Seq[EvaluationUser], label: String, replyTo: Long): JsObject =
    apply(message(domain, Seq((evaluationUsers, label))), replyTo)

  def apply(domain: String, groupedEvaluationUsersWithLabel: Seq[(Seq[EvaluationUser], String)], replyTo: Long): JsObject =
    apply(message(domain, groupedEvaluationUsersWithLabel), replyTo)

  private def apply(message: String, replyTo: Long): JsObject =
    Json.obj(
      "message" -> message,
      "replyTo" -> replyTo,
    )

  private def message(domain: String, label: String, evaluationUsers: Seq[EvaluationUser]): String =
    (evaluationUsers zip Stream.from(1)).map(tupled((evaluationUser, order) =>
      s"$order. ${userNameView(domain)(evaluationUser)} ( ${evaluationUser.point} points )"
    ))
      .+:(label)
      .mkString("\n")

  private def message(domain: String, groupedEvaluationUsersWithLabel: Seq[(Seq[EvaluationUser], String)]): String =
    (
      for ((evaluationUsers, label) <- groupedEvaluationUsersWithLabel)
        yield message(domain, label, evaluationUsers)
    ).mkString(s"$Separation\n", s"\n$Separation\n", s"\n$Separation")

  private def userNameView(domain: String): EvaluationUser => String = {
    case EvaluationUser(User(_, Some(userId), name), _) => s"[$name](https://$domain/user/$userId)"
    case EvaluationUser(User(_, None, name), _) => name
  }

}




