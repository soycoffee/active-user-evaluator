package controllers.evaluation

import models._
import play.api.i18n.Lang
import play.api.libs.json._
import play.api.mvc._
import services.{EvaluationAggregator, TypetalkMessageBuilder, UseApiDestination}

import scala.concurrent.ExecutionContext

trait TypetalkController extends BaseController with HasTargetActivityTypes {

  import TypetalkWebhookBody.reads

  val useApiDestination: UseApiDestination
  val evaluationAggregator: EvaluationAggregator
  val typetalkMessageBuilder: TypetalkMessageBuilder

  def typetalkMessageLabelKey: String

  private implicit lazy val ec: ExecutionContext = defaultExecutionContext
  private implicit lazy val lang: Lang = supportedLangs.availables.head

  lazy val typetalkMessageLabel : String =
    messagesApi(typetalkMessageLabelKey)

  def typetalkWebhook(projectId: String, apiKey: String): Action[TypetalkWebhookBody] = Action.async(parse.json[TypetalkWebhookBody]) { implicit request =>
    val TypetalkWebhookBody(count, sinceBeforeDays, replyFrom) = request.body
    useApiDestination(apiKey) { implicit destination =>
      evaluationAggregator.queryEvaluationUsers(targetActivityTypes, projectId, count, sinceBeforeDays)
        .map(typetalkMessageBuilder(typetalkMessageLabel, _))
        .map(buildResponseBody(_, replyFrom))
        .map(Ok(_))
    }
  }

  private def buildResponseBody(message: String, replyTo: Long): JsObject =
    Json.obj(
      "message" -> message,
      "replyTo" -> replyTo,
    )

}
