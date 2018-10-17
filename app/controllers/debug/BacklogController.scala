package controllers.debug

import actions.OnlyDebug
import javax.inject._
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

  def queryActivities(userId: Long, apiKey: String): Action[AnyContent] = onlyDebug.async {
    useApiDestination(apiKey) { implicit destination =>
      backlogApiClient.queryUsersActivitiesAsJson(userId)
        .map(Ok(_))
    }
  }

}
