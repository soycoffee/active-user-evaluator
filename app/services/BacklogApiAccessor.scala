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

}