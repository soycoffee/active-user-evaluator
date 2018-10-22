package controllers.evaluation

import models._
import play.api.libs.json.Json
import play.api.mvc._
import services.{EvaluationAggregator, UseApiDestination}

import scala.concurrent.ExecutionContext

trait JsonController extends BaseController {

  val useApiDestination: UseApiDestination
  val evaluationAggregator: EvaluationAggregator
  implicit val ec: ExecutionContext

  def targetActivityTypes: Seq[Activity.Type]

  def index(projectId: String, count: Option[Int], sinceBeforeDays: Option[Int], apiKey: String): Action[AnyContent] = Action.async {
    useApiDestination(apiKey) { implicit destination =>
      evaluationAggregator.queryEvaluationUsers(projectId, targetActivityTypes, count, sinceBeforeDays)
        .map(Json.toJson(_))
        .map(Ok(_))
    }
  }

}

object JsonController {

  trait WithTypeGroup extends JsonController {

    def targetActivityTypeGroup: Activity.TypeGroup

    lazy val targetActivityTypes: Seq[Activity.Type] =
      Activity.Type.Values.filter(_.group == targetActivityTypeGroup)

  }

}
