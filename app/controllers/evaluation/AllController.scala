package controllers.evaluation

import javax.inject._
import models.Activity
import models.typetalk.WebhookRequestBody
import play.api.i18n.Lang
import play.api.mvc._
import services.typetalk.WebhookResponseBodyBuilder
import services.{EvaluationAggregator, UseApiDestination}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AllController @Inject()(
                               useApiDestination: UseApiDestination,
                               evaluationAggregator: EvaluationAggregator,
                               webhookResponseBuilder: WebhookResponseBodyBuilder,
                             ) extends InjectedController {

  import AllController._

  private implicit lazy val ec: ExecutionContext = defaultExecutionContext
  private implicit lazy val lang: Lang = supportedLangs.availables.head

  def typetalkWebhook(projectId: String, apiKey: String): Action[WebhookRequestBody] = Action.async(parse.json[WebhookRequestBody]) { implicit request =>
    val WebhookRequestBody(count, sinceBeforeDays, replyFrom) = request.body
    useApiDestination(apiKey) { implicit destination =>
      Future.sequence(
        groupedActivityTypes
          .map(evaluationAggregator.queryEvaluationUsers(_, projectId, count, sinceBeforeDays))
      )
        .map(_ zip ActivityTypeGroupLabelKeys)
        .map(webhookResponseBuilder(destination.domain, _, replyFrom))
        .map(Ok(_))
    }
  }

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

  private lazy val groupedActivityTypes: Seq[Seq[Activity.Type]] =
    ActivityTypeGroups.map(group => Activity.Type.Values.filter(_.group == group))

}