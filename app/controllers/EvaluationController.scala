package controllers

import javax.inject._
import models.Activity
import play.api.mvc._
import services.{ActivityPointJudge, BacklogApiClient, UseApiDestination}

import scala.concurrent.ExecutionContext

@Singleton
class EvaluationController @Inject()(
                                      val useApiDestination: UseApiDestination,
                                      val backlogApiClient: BacklogApiClient,
                                      val activityPointAggregator: ActivityPointJudge,
                                      val ec: ExecutionContext,
                                    ) extends InjectedController with BaseEvaluationController {

  override val targetActivityTypes: Seq[Activity.Type] = Seq(Activity.Type.CreateGitPush)

}
