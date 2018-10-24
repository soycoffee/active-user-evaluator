package controllers.evaluation

import javax.inject._
import models.{Activity, TypetalkWebhookBody}
import play.api.i18n.Lang
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import services.{EvaluationAggregator, TypetalkMessageBuilder, UseApiDestination}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AllController @Inject()(
                               useApiDestination: UseApiDestination,
                               evaluationAggregator: EvaluationAggregator,
                               typetalkMessageBuilder: TypetalkMessageBuilder,
                             ) extends InjectedController {

  import AllController._

  private implicit lazy val ec: ExecutionContext = defaultExecutionContext
  private implicit lazy val lang: Lang = supportedLangs.availables.head

  def typetalkWebhook(projectId: String, apiKey: String): Action[TypetalkWebhookBody] = Action.async(parse.json[TypetalkWebhookBody]) { implicit request =>
    val TypetalkWebhookBody(count, sinceBeforeDays, replyFrom) = request.body
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