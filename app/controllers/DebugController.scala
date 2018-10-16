package controllers

import actions.OnlyDebug
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import services.{BacklogApiAccessor, GitCommandExecutor}

import scala.concurrent.ExecutionContext

@Singleton
class DebugController @Inject()(onlyDebug: OnlyDebug, backlogApiAccessor: BacklogApiAccessor, gitCommandExecutor: GitCommandExecutor)(implicit ec: ExecutionContext) extends InjectedController {

  def queryGitRepositories(projectId: String, apiKey: String): Action[AnyContent] = onlyDebug.async {
    backlogApiAccessor.queryGitRepositoriesAsJson(projectId, apiKey)
      .map(Ok(_))
  }

  def fetchGitRepositories(projectId: String, apiKey: String): Action[AnyContent] = onlyDebug.async {
    backlogApiAccessor.queryGitRepositories(projectId, apiKey)
      .map(_.map(repository => gitCommandExecutor.temporaryFetch(repository.httpUrl).toString))
      .map(Json.toJson(_))
      .map(Ok(_))
  }

}