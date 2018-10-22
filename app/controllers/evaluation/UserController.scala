package controllers.evaluation

import play.api.libs.json.Json
import play.api.mvc._
import services.{EvaluationAggregator, UseApiDestination}

import scala.concurrent.ExecutionContext

trait UserController extends BaseController with HasTargetActivityTypes {

  val useApiDestination: UseApiDestination
  val evaluationAggregator: EvaluationAggregator
  implicit val ec: ExecutionContext

  def queryUsers(projectId: String, count: Option[Int], sinceBeforeDays: Option[Int], apiKey: String): Action[AnyContent] = Action.async {
    useApiDestination(apiKey) { implicit destination =>
      evaluationAggregator.queryEvaluationUsers(projectId, targetActivityTypes, count, sinceBeforeDays)
        .map(Json.toJson(_))
        .map(Ok(_))
    }
  }

}
