package controllers.evaluation

import javax.inject._
import models.typetalk.WebhookRequestBody
import models.{Activity, EvaluationUser}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc._
import services.typetalk.WebhookResponseBodyBuilder
import services.{EvaluationAggregator, FutureSerializer, UseApiDestination}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

@Singleton
class AllController @Inject()(
                               configuration: Configuration,
                               useApiDestination: UseApiDestination,
                               evaluationAggregator: EvaluationAggregator,
                               webhookResponseBuilder: WebhookResponseBodyBuilder,
                               futureSerializer: FutureSerializer,
                             ) extends InjectedController {

  import AllController._

  private implicit val backlogApiInterval: Option[FiniteDuration] = configuration.getOptional[FiniteDuration]("backlog.api.interval")
  private implicit lazy val ec: ExecutionContext = defaultExecutionContext
  private implicit lazy val lang: Lang = supportedLangs.availables.head

  private lazy val activityTypeGroupLabels: Seq[String] =
    activityTypeGroupLabelKeys.map(messagesApi(_))

  def typetalkWebhook(projectKey: String, apiKey: String): Action[WebhookRequestBody] = Action.async(parse.json[WebhookRequestBody]) { implicit request =>
    val WebhookRequestBody(count, sinceBeforeDays, replyFrom) = request.body
    useApiDestination(apiKey) { implicit destination =>
      futureSerializer[Seq[Activity.Type], Seq[EvaluationUser]](groupedActivityTypes)({
        evaluationAggregator.queryEvaluationUsers(_, projectKey, count, sinceBeforeDays)
      })
        .map(_ zip activityTypeGroupLabels)
        .map(webhookResponseBuilder(destination.domain, _, replyFrom))
        .map(Ok(_))
    }
  }

}

object AllController {

  private lazy val groupedActivityTypes: Seq[Seq[Activity.Type]] =
    Seq(
      Activity.TypeGroup.Management,
      Activity.TypeGroup.Document,
      Activity.TypeGroup.Implement,
    )
      .map(group => Activity.Type.Values.filter(_.group == group))

  private val activityTypeGroupLabelKeys = Seq(
    "evaluation.management.typetalk.message.label",
    "evaluation.document.typetalk.message.label",
    "evaluation.implement.typetalk.message.label",
  )

}