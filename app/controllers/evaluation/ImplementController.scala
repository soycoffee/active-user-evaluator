package controllers.evaluation

import javax.inject._
import models.Activity
import play.api.mvc._
import services.{EvaluationAggregator, UseApiDestination}

import scala.concurrent.ExecutionContext

@Singleton
class ImplementController @Inject()(
                                     val useApiDestination: UseApiDestination,
                                     val evaluationAggregator: EvaluationAggregator,
                                    )(implicit val ec: ExecutionContext) extends InjectedController
  with UserController
  with TypetalkController
  with HasTargetActivityTypeGroup
{

  override val targetActivityTypeGroup: Activity.TypeGroup = Activity.TypeGroup.Implement
  override val typetalkMessageLabelKey: String = "evaluation.implement.typetalk.message.label"

}
