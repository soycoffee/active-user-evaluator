package controllers

import actions.OnlyDebug
import javax.inject._
import play.api.mvc._
import services.BacklogApiAccessor

import scala.concurrent.ExecutionContext

/**
  * デバッグ用の操作を実装する。
  * 本番環境 [[play.api.Mode.Prod]] で使用されないように、すべてのアクションに [[actions.OnlyDebug]] を適用する。
  */
@Singleton
class DebugController @Inject()(onlyDebug: OnlyDebug, backlogApiAccessor: BacklogApiAccessor)(implicit ec: ExecutionContext) extends InjectedController {

  def queryUsers(projectId: String, apiKey: String): Action[AnyContent] = onlyDebug.async {
    backlogApiAccessor.queryProjectUsersAsJson(projectId, apiKey)
      .map(Ok(_))
  }

  def queryActivities(userId: Long, apiKey: String): Action[AnyContent] = onlyDebug.async {
    backlogApiAccessor.queryUsersActivitiesAsJson(userId, apiKey)
      .map(Ok(_))
  }

}
