package controllers.evaluation

import javax.inject._
import models.Activity
import models.typetalk.WebhookRequestBody
import play.api.i18n.Lang
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import services.typetalk.WebhookResponseBodyBuilder
import services.{EvaluationAggregator, UseApiDestination}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AllController @Inject()(
                               useApiDestination: UseApiDestination,
                               evaluationAggregator: EvaluationAggregator,
                               typetalkMessageBuilder: WebhookResponseBodyBuilder,
                             ) extends InjectedController {

  import AllController._

  private implicit lazy val ec: ExecutionContext = defaultExecutionContext
  private implicit lazy val lang: Lang = supportedLangs.availables.head

  def typetalkWebhook(projectId: String, apiKey: String): Action[WebhookRequestBody] = Action.async(parse.json[WebhookRequestBody]) { implicit request =>
    val WebhookRequestBody(count, sinceBeforeDays, replyFrom) = request.body
    useApiDestination(apiKey) { implicit destination =>
      Future.sequence(
        for ((activityTypes, label) <-  groupedActivityTypesWithLabel) yield {
          evaluationAggregator.queryEvaluationUsers(activityTypes, projectId, count, sinceBeforeDays)
            .map(typetalkMessageBuilder(label, _))
        }
      )
        .map(buildResponseBody(_, replyFrom))
        .map(Ok(_))
    }
  }

  private lazy val groupedActivityTypesWithLabel: Seq[(Seq[Activity.Type], String)] =
    ActivityTypeGroups.map(group => Activity.Type.Values.filter(_.group == group)) zip ActivityTypeGroupLabelKeys.map(messagesApi(_))

  private def buildResponseBody(messages: Seq[String], replyTo: Long): JsObject =
    Json.obj(
      "message" -> messages.mkString("\n\n"),
      "replyTo" -> replyTo,
    )

}

object AllController {

  val ActivityTypeGroups = Seq(
    Activity.TypeGroup.Management,
    Activity.TypeGroup.Document,
    Activity.TypeGroup.Implement,
  )

  val ActivityTypeGroupLabelKeys = Seq(
    "evaluation.management.typetalk.message.label",
    "evaluation.document.typetalk.message.label",
    "evaluation.implement.typetalk.message.label",
  )

}