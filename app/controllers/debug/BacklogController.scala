package controllers.debug

import actions.OnlyDebug
import javax.inject._
import models.Activity
import play.api.mvc._
import services.{BacklogApiClient, UseApiDestination}

import scala.concurrent.ExecutionContext

@Singleton
class BacklogController @Inject()(onlyDebug: OnlyDebug, backlogApiClient: BacklogApiClient, useApiDestination: UseApiDestination)(implicit ec: ExecutionContext) extends InjectedController {

  def queryUsers(projectId: String, apiKey: String): Action[AnyContent] = onlyDebug.async {
    useApiDestination(apiKey) { implicit destination =>
      backlogApiClient.queryProjectUsersAsJson(projectId)
        .map(Ok(_))
    }
  }

  def queryActivities(userId: Long, activityTypeId: Int, apiKey: String): Action[AnyContent] = onlyDebug.async {
    useApiDestination(apiKey) { implicit destination =>
      backlogApiClient.queryUserActivitiesAsJson(userId, Activity.Type.Values.find(_.id == activityTypeId).get)
        .map(Ok(_))
    }
  }

}
