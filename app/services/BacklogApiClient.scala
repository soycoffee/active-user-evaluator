package services

import javax.inject._
import models.{Activity, User}
import play.api.Logger
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient

import scala.Function.tupled
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BacklogApiClient @Inject()(ws: WSClient)(implicit ec: ExecutionContext) {

  import BacklogApiClient._
  import User.format

  private val logger = Logger(this.getClass)

  def queryProjectUsers(projectKey: String)(implicit destination: Destination): Future[Seq[User]] =
    queryProjectUsersAsJson(projectKey)
      .map(_.as[Seq[User]])

  def queryProjectUsersAsJson(projectKey: String)(implicit destination: Destination): Future[JsValue] =
    access("GET", makeApiUrl(s"/projects/$projectKey/users"))

  def queryUserActivities(userId: Long, types: Seq[Activity.Type])(implicit destination: Destination): Future[Seq[Activity]] =
    queryUserActivitiesAsJson(userId, types)
      .map(_.as[Seq[Activity]])

  def queryUserActivitiesAsJson(userId: Long, types: Seq[Activity.Type])(implicit destination: Destination): Future[JsValue] =
    access("GET", makeApiUrl(
      s"/users/$userId/activities",
      types.map(`type` => "activityTypeId[]" -> s"${`type`.id}")
        .:+("count" -> "100"): _*,
    ))

  private def access(method: String, url: String): Future[JsValue] = {
    val request = ws.url(url)
      .withMethod(method)
    logger.info(s"Access Backlog: ${request.method} ${request.uri}")
    request
      .execute()
      .map(_.body[JsValue])
  }

  // WsClient で Query String を付加するとエンコードされてしまうため、直接URLに含める。
  private def makeApiUrl(path: String, queryStringParameters: (String, String)*)(implicit destination: Destination) =
    s"https://${destination.domain}/api/v2$path${makeQueryString(queryStringParameters :+ "apiKey" -> destination.key: _*)}"

  private def makeQueryString(parameters: (String, String)*): String =
    parameters match {
      case Seq() => ""
      case Seq((headKey, headValue), tail @ _*) => s"?$headKey=$headValue" + tail.map(tupled((key, value) => s"&$key=$value")).mkString
    }

}

object BacklogApiClient {

  case class Destination(domain: String, key: String)

}