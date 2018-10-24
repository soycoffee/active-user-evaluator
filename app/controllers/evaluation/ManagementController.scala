package controllers.evaluation

import javax.inject._
import models.Activity
import play.api.mvc._
import services.typetalk.WebhookResponseBodyBuilder
import services.{EvaluationAggregator, UseApiDestination}

@Singleton
class ManagementController @Inject()(
                                      val useApiDestination: UseApiDestination,
                                      val evaluationAggregator: EvaluationAggregator,
                                      val typetalkMessageBuilder: WebhookResponseBodyBuilder,
                                    ) extends InjectedController
  with UserController
  with TypetalkController
  with HasTargetActivityTypeGroup
{

  override val targetActivityTypeGroup: Activity.TypeGroup = Activity.TypeGroup.Management
  override val typetalkMessageLabelKey = "evaluation.management.typetalk.message.label"

}
