package controllers.evaluation

import javax.inject._
import models.Activity
import play.api.mvc._
import services.typetalk.WebhookResponseBodyBuilder
import services.{EvaluationAggregator, UseApiDestination}

@Singleton
class DocumentController @Inject()(
                                    val useApiDestination: UseApiDestination,
                                    val evaluationAggregator: EvaluationAggregator,
                                    val webhookResponseBuilder: WebhookResponseBodyBuilder,
                                  ) extends InjectedController
  with UserController
  with TypetalkController
  with HasTargetActivityTypeGroup
{

  override val targetActivityTypeGroup: Activity.TypeGroup = Activity.TypeGroup.Document
  override val typetalkMessageLabelKey: String = "evaluation.document.typetalk.message.label"

}
