package controllers.evaluation

import play.api.libs.json.Json
import play.api.mvc._
import services.{EvaluationAggregator, UseApiDestination}

import scala.concurrent.ExecutionContext

trait UserController extends BaseController with HasTargetActivityTypes {

  val useApiDestination: UseApiDestination
  val evaluationAggregator: EvaluationAggregator

  private implicit lazy val ec: ExecutionContext = defaultExecutionContext

  def queryUsers(projectKey: String, count: Option[Int], sinceBeforeDays: Option[Int], apiKey: String): Action[AnyContent] = Action.async {
    useApiDestination(apiKey) { implicit destination =>
      evaluationAggregator.queryEvaluationUsers(targetActivityTypes, projectKey, count, sinceBeforeDays)
        .map(Json.toJson(_))
        .map(Ok(_))
    }
  }

}
