package controllers

import javax.inject._
import play.api.mvc._
import services.{ActivityPointAggregator, BacklogApiClient, UseApiDestination}

import scala.concurrent.ExecutionContext

@Singleton
class EvaluationController @Inject()(
                                      val useApiDestination: UseApiDestination,
                                      val backlogApiClient: BacklogApiClient,
                                      val activityPointAggregator: ActivityPointAggregator,
                                      val ec: ExecutionContext,
                                    ) extends InjectedController with BaseEvaluationController
