package controllers.evaluation

import models._
import play.api.i18n.Lang
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import services.{BacklogApiClient, EvaluationAggregator, UseApiDestination}

import scala.Function.tupled
import scala.concurrent.ExecutionContext
import scala.util.Try

trait TypetalkController extends BaseController with HasTargetActivityTypes {

  import controllers.evaluation.TypetalkController.WebhookBody
  import WebhookBody.reads

  val useApiDestination: UseApiDestination
  val evaluationAggregator: EvaluationAggregator

  def typetalkMessageLabelKey: String

  private implicit lazy val ec: ExecutionContext = defaultExecutionContext

  private implicit lazy val lang: Lang = supportedLangs.availables.head

  def typetalkWebhook(projectId: String, apiKey: String): Action[WebhookBody] = Action.async(parse.json[WebhookBody]) { implicit request =>
    val WebhookBody(count, sinceBeforeDays, replyFrom) = request.body
    useApiDestination(apiKey) { implicit destination =>
      evaluationAggregator.queryEvaluationUsers(projectId, targetActivityTypes, count, sinceBeforeDays)
        .map(buildResponseBody(_, replyFrom))
        .map(Json.toJson(_))
        .map(Ok(_))
    }
  }

  lazy val typetalkMessageLabel : String =
    messagesApi(typetalkMessageLabelKey)

  private def buildResponseBody(evaluationUsers: Seq[EvaluationUser], replyTo: Long)(implicit destination: BacklogApiClient.Destination): JsObject =
    Json.obj(
      "message" -> buildMessage(evaluationUsers),
      "replyTo" -> replyTo,
    )

  private def buildMessage(evaluationUsers: Seq[EvaluationUser])(implicit destination: BacklogApiClient.Destination): String =
    (evaluationUsers zip Stream.from(1)).map(tupled((evaluationUser, order) =>
      s"$order. [${evaluationUser.name}](https://${destination.domain}/user/${evaluationUser.userId}) ( ${evaluationUser.point} points )"
    ))
      .+:(typetalkMessageLabel)
      .mkString("\n")

}

object TypetalkController {

  case class WebhookBody(count: Option[Int], sinceBeforeDays: Option[Int], replyFrom: Long)

  object WebhookBody {

    def apply(message: String, replyFrom: Long): WebhookBody = {
      val pickInt = (key: String) =>
        s"""$key=\\S+""".r.findFirstIn(message).flatMap(Try(_).map(_.split("=")(1).toInt).toOption)
      WebhookBody(pickInt("count"), pickInt("sinceBeforeDays"), replyFrom)
    }

    implicit val reads: Reads[WebhookBody] = (
      (__ \ "post" \ "message").read[String] and
        (__ \ "post" \ "account" \ "id").read[Long]
      ) (WebhookBody(_: String, _: Long))

  }

}


