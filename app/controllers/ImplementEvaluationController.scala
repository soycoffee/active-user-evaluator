package controllers

import javax.inject._
import models.Activity
import play.api.mvc._
import services.{EvaluationAggregator, UseApiDestination}

import scala.concurrent.ExecutionContext

@Singleton
class ImplementEvaluationController @Inject()(
                                      val useApiDestination: UseApiDestination,
                                      val evaluationAggregator: EvaluationAggregator,
                                      val ec: ExecutionContext,
                                    ) extends InjectedController with EvaluationController.WithTypeGroup {

  override val targetActivityTypeGroup: Activity.TypeGroup = Activity.TypeGroup.Implement

}
