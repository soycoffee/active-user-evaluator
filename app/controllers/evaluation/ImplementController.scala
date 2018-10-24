package controllers.evaluation

import javax.inject._
import models.Activity
import play.api.mvc._
import services.typetalk.WebhookResponseBodyBuilder
import services.{EvaluationAggregator, UseApiDestination}

@Singleton
class ImplementController @Inject()(
                                     val useApiDestination: UseApiDestination,
                                     val evaluationAggregator: EvaluationAggregator,
                                     val typetalkMessageBuilder: WebhookResponseBodyBuilder,
                                    ) extends InjectedController
  with UserController
  with TypetalkController
  with HasTargetActivityTypeGroup
{

  override val targetActivityTypeGroup: Activity.TypeGroup = Activity.TypeGroup.Implement
  override val typetalkMessageLabelKey: String = "evaluation.implement.typetalk.message.label"

}
