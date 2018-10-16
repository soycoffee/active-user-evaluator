package controllers

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import services.{BacklogApiAccessor, GitCommandExecutor}

import scala.concurrent.ExecutionContext

@Singleton
class ApiController @Inject()(backlogApiAccessor: BacklogApiAccessor, gitCommandExecutor: GitCommandExecutor)(implicit ec: ExecutionContext) extends InjectedController {

//  def index(projectId: String, apiKey: String, authors: String, sinceBeforeDays: Int): Action[AnyContent] = Action.async {
//    backlogApiAccessor.queryGitRepositories(projectId, apiKey).map({ remoteRepositories =>
//      remoteRepositories.map({ remoteRepository =>
//        (for {
//          localRepositoryPath <- gitCommandExecutor.temporaryFetch(remoteRepository.httpUrl)
//          commitsCount <- gitCommandExecutor.readCommitsCount(localRepositoryPath, authors, sinceBeforeDays)
//          changesCountSummary <- gitCommandExecutor.readChangesCountSummary(localRepositoryPath, authors, sinceBeforeDays)
//        } yield {
//          Json.obj(
//            "commits_count" -> commitsCount,
//            "additions_count" -> changesCountSummary.addition,
//            "deletions_count" -> changesCountSummary.deletion,
//          )
//        }).get
//      })
//    })
//      .map(Json.toJson(_))
//      .map(Ok(_))
//  }

  def index() = Action {
    Ok("ok")
  }

}
