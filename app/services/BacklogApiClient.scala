package services

import javax.inject._
import models.{Activity, User}
import play.api.Logger
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BacklogApiClient @Inject()(ws: WSClient)(implicit ec: ExecutionContext) {

  import BacklogApiClient._

  private val logger = Logger(this.getClass)

  def queryProjectUsers(projectId: String)(implicit destination: Destination): Future[Seq[User]] =
    queryProjectUsersAsJson(projectId)
      .map(_.as[Seq[User]])

  def queryProjectUsersAsJson(projectId: String)(implicit destination: Destination): Future[JsValue] =
    access("GET", s"/projects/$projectId/users")

  def queryUserActivities(userId: Long)(implicit destination: Destination): Future[Seq[Activity]] =
    queryUserActivitiesAsJson(userId)
      .map(_.as[Seq[Activity]])

  def queryUserActivitiesAsJson(userId: Long)(implicit destination: Destination): Future[JsValue] =
    access("GET", s"/users/$userId/activities")

  private def access(method: String, path: String)(implicit destination: Destination): Future[JsValue] = {
    val request = ws.url(apiUrl(destination.domain, path))
      .withMethod(method)
      .withQueryStringParameters("apiKey" -> destination.key)
    logger.info(s"Access Backlog: ${request.url}")
    request
      .execute()
      .map(_.body[JsValue])
  }

  private def apiUrl(domain: String, endPoint: String) =
    s"https://$domain/api/v2$endPoint"

}

object BacklogApiClient {

  case class Destination(domain: String, key: String)

}