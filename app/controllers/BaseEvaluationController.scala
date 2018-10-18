package controllers

import models._
import play.api.libs.json.Json
import play.api.mvc._
import services.{ActivityPointAggregator, BacklogApiClient, UseApiDestination}

import scala.concurrent.{ExecutionContext, Future}

trait BaseEvaluationController extends BaseController {

  val useApiDestination: UseApiDestination
  val backlogApiClient: BacklogApiClient
  val activityPointAggregator: ActivityPointAggregator
  implicit val ec: ExecutionContext

  val targetActivityTypes: Seq[Activity.Type]

  def index(projectId: String, sinceBeforeDays: Option[Int], apiKey: String): Action[AnyContent] = Action.async {
    useApiDestination(apiKey) { implicit destination =>
      backlogApiClient.queryProjectUsers(projectId).map({ users =>
        users.map({ user =>
          backlogApiClient.queryUserActivities(user.id, Activity.Type.CreateGitPush).map({ activities =>
            evaluateUser(user, activities)
          })
        })
      })
        .flatMap(Future.sequence(_))
        .map(Json.toJson(_))
        .map(Ok(_))
    }
  }

//  private def xxxx(userId: Long, sinceBeforeDays: Int) = {
//    targetActivityTypes.map(backlogApiClient)
//  }

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
