package controllers.evaluation

import models.typetalk.WebhookRequestBody
import play.api.i18n.Lang
import play.api.libs.json._
import play.api.mvc._
import services.typetalk.WebhookResponseBodyBuilder
import services.{EvaluationAggregator, UseApiDestination}

import scala.concurrent.ExecutionContext

trait TypetalkController extends BaseController with HasTargetActivityTypes {

  import WebhookRequestBody.reads

  val useApiDestination: UseApiDestination
  val evaluationAggregator: EvaluationAggregator
  val webhookResponseBuilder: WebhookResponseBodyBuilder

  val typetalkMessageLabelKey: String

  private implicit lazy val ec: ExecutionContext = defaultExecutionContext
  private implicit lazy val lang: Lang = supportedLangs.availables.head

  lazy val typetalkMessageLabel : String =
    messagesApi(typetalkMessageLabelKey)

  def typetalkWebhook(projectKey: String, apiKey: String): Action[WebhookRequestBody] = Action.async(parse.json[WebhookRequestBody]) { implicit request =>
    val WebhookRequestBody(count, sinceBeforeDays, replyFrom) = request.body
    useApiDestination(apiKey) { implicit destination =>
      evaluationAggregator.queryEvaluationUsers(targetActivityTypes, projectKey, count, sinceBeforeDays)
        .map(webhookResponseBuilder(destination.domain, _, typetalkMessageLabel, replyFrom))
        .map(Ok(_))
    }
  }

}
