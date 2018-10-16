package services

import javax.inject._
import play.api.Logger
import play.api.libs.json.{JsValue, Json, Reads}
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BacklogApiAccessor @Inject()(implicit ws: WSClient, ec: ExecutionContext) {

  import BacklogApiAccessor._

  private val baseApiUrl = "https://nulab-exam.backlog.jp/api/v2"
  private val logger = Logger(this.getClass)

  def queryProjectUsers(projectId: String, apiKey: String): Future[Seq[User]] =
    queryProjectUsersAsJson(projectId, apiKey)
      .map(_.as[Seq[User]])

  def queryProjectUsersAsJson(projectId: String, apiKey: String): Future[JsValue] =
    access("GET", s"/projects/$projectId/users", apiKey)

  def queryGitRepositories(projectId: String, apiKey: String): Future[Seq[GitRepository]] =
    queryGitRepositoriesAsJson(projectId, apiKey)
      .map(_.as[Seq[GitRepository]])

  def queryGitRepositoriesAsJson(projectId: String, apiKey: String): Future[JsValue] =
    access("GET", s"/projects/$projectId/git/repositories", apiKey)

  private def access(method: String, endPoint: String, apiKey: String): Future[JsValue] = {
    val request = ws.url(s"$baseApiUrl$endPoint")
      .withMethod(method)
      .withQueryStringParameters("apiKey" -> apiKey)
    logger.info(s"Access Backlog: ${request.url}")
    request
      .execute()
      .map(_.body[JsValue])
  }

}

object BacklogApiAccessor {

  case class User(httpUrl: String)

  case class GitRepository(httpUrl: String)

  private implicit val gitRepositoryReads: Reads[GitRepository] = Json.reads[GitRepository]
  private implicit val userReads: Reads[User] = Json.reads[User]

  private abstract class ActivityType(id: Int)

  private object ActivityType {

    case object CreateIssue extends ActivityType(1)
    case object UpdateIssue extends ActivityType(2)
    case object CreateIssueComment extends ActivityType(3)
    case object CreateWiki extends ActivityType(5)
    case object UpdateWiki extends ActivityType(6)
    case object CreateFile extends ActivityType(8)
    case object UpdateFile extends ActivityType(9)
    case object CreateGitPush extends ActivityType(12)
    case object CreateGitRepository extends ActivityType(13)
    case object UpdateMultiIssue extends ActivityType(14)
    case object CreatePullRequest extends ActivityType(18)
    case object UpdatePullRequest extends ActivityType(19)
    case object CreatePullRequestComment extends ActivityType(20)
    case object CreateVersion extends ActivityType(22)
    case object UpdateVersion extends ActivityType(23)

    val managementItems = Seq(
      CreateIssue,
      UpdateIssue,
      CreateIssueComment,
      UpdateMultiIssue,
      CreateVersion,
      UpdateVersion
    )

    val documentItems = Seq(
      CreateWiki,
      UpdateWiki,
      CreateFile,
      UpdateFile,
    )

    val implementItems = Seq(
      CreateGitPush,
      CreateGitRepository,
      CreatePullRequest,
      UpdatePullRequest,
      CreatePullRequestComment,
    )

  }

}