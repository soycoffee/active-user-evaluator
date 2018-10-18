package controllers

import javax.inject._
import models.Activity
import play.api.mvc._
import services.{EvaluationAggregator, UseApiDestination}

import scala.concurrent.ExecutionContext

@Singleton
class EvaluationController @Inject()(
                                      val useApiDestination: UseApiDestination,
                                      val evaluationAggregator: EvaluationAggregator,
                                      val ec: ExecutionContext,
                                    ) extends InjectedController with BaseEvaluationController {

  override val targetActivityTypes: Seq[Activity.Type] = Seq(Activity.Type.CreateGitPush)

}
