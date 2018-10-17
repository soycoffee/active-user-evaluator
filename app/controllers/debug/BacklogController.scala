package controllers.debug

import actions.OnlyDebug
import javax.inject._
import play.api.mvc._
import services.{BacklogApiAccessor, UseApiDestination}

import scala.concurrent.ExecutionContext

@Singleton
class BacklogController @Inject()(onlyDebug: OnlyDebug, backlogApiAccessor: BacklogApiAccessor, useApiDestination: UseApiDestination)(implicit ec: ExecutionContext) extends InjectedController {

  def queryUsers(projectId: String, apiKey: String): Action[AnyContent] = onlyDebug.async {
    useApiDestination(apiKey) { implicit destination =>
      backlogApiAccessor.queryProjectUsersAsJson(projectId)
        .map(Ok(_))
    }
  }

  def queryActivities(userId: Long, apiKey: String): Action[AnyContent] = onlyDebug.async {
    useApiDestination(apiKey) { implicit destination =>
      backlogApiAccessor.queryUsersActivitiesAsJson(userId)
        .map(Ok(_))
    }
  }

}
