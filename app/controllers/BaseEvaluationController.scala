package controllers

import models._
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._
import services.{EvaluationAggregator, UseApiDestination}

import scala.concurrent.ExecutionContext

trait BaseEvaluationController extends BaseController {

  val configuration: Configuration
  val useApiDestination: UseApiDestination
  val evaluationAggregator: EvaluationAggregator
  implicit val ec: ExecutionContext

  val targetActivityTypes: Seq[Activity.Type]

  private val defaultSinceBeforeDays: Int = configuration.get[Int]("evaluation.default.sinceBeforeDays")

  def index(projectId: String, sinceBeforeDays: Option[Int], apiKey: String): Action[AnyContent] = Action.async {
    useApiDestination(apiKey) { implicit destination =>
      evaluationAggregator.queryEvaluationUsers(projectId, sinceBeforeDays.getOrElse(defaultSinceBeforeDays), targetActivityTypes)
        .map(Json.toJson(_))
        .map(Ok(_))
    }
  }

}
