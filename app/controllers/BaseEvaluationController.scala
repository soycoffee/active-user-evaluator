package controllers

import models._
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import services.{ActivityPointAggregator, BacklogApiClient, UseApiDestination}

import scala.concurrent.{ExecutionContext, Future}

trait BaseEvaluationController extends BaseController {

  import Activity.writes
  import User.format

  val useApiDestination: UseApiDestination
  val backlogApiClient: BacklogApiClient
  val activityPointAggregator: ActivityPointAggregator
  implicit val ec: ExecutionContext

  def index(projectId: String, sinceBeforeDays: Option[Int], apiKey: String): Action[AnyContent] = Action.async {
    useApiDestination(apiKey) { implicit destination =>
      backlogApiClient.queryProjectUsers(projectId).map({ users =>
        users.map({ user =>
          backlogApiClient.queryUserActivities(user.id).map({ activities =>
            evaluateUser(user, activities)
          })
        })
      })
        .flatMap(Future.sequence(_))
        .map(Json.toJson(_))
        .map(Ok(_))
    }
  }

  private def evaluateUser(user: User, activities: Seq[Activity]) = {
    val evaluationActivities = activities.map(evaluateActivity)
    EvaluationUser(
      user,
      evaluationActivities,
      0,
    )
  }

  private def evaluateActivity(activity: Activity): EvaluationActivity =
    EvaluationActivity(activity, activityPointAggregator(activity))

}
