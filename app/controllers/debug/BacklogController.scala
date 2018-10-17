package controllers.debug

import actions.OnlyDebug
import javax.inject._
import play.api.mvc._
import services.BacklogApiAccessor

import scala.concurrent.ExecutionContext

@Singleton
class BacklogController @Inject()(onlyDebug: OnlyDebug, backlogApiAccessor: BacklogApiAccessor)(implicit ec: ExecutionContext) extends InjectedController {

  def queryUsers(projectId: String, apiKey: String): Action[AnyContent] = onlyDebug.async {
    backlogApiAccessor.queryProjectUsersAsJson(projectId, apiKey)
      .map(Ok(_))
  }

  def queryActivities(userId: Long, apiKey: String): Action[AnyContent] = onlyDebug.async {
    backlogApiAccessor.queryUsersActivitiesAsJson(userId, apiKey)
      .map(Ok(_))
  }

}
