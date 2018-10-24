package controllers.evaluation

import javax.inject._
import models.Activity
import play.api.mvc._
import services.{EvaluationAggregator, TypetalkMessageBuilder, UseApiDestination}

@Singleton
class DocumentController @Inject()(
                                    val useApiDestination: UseApiDestination,
                                    val evaluationAggregator: EvaluationAggregator,
                                    val typetalkMessageBuilder: TypetalkMessageBuilder,
                                  ) extends InjectedController
  with UserController
  with TypetalkController
  with HasTargetActivityTypeGroup
{

  override val targetActivityTypeGroup: Activity.TypeGroup = Activity.TypeGroup.Document
  override val typetalkMessageLabelKey: String = "evaluation.document.typetalk.message.label"

}
