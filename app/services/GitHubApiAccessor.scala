package services

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import javax.inject._
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient
import play.api.{Configuration, Logger}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GitHubApiAccessor @Inject()(implicit ws: WSClient, configuration: Configuration, ec: ExecutionContext) {

  private val baseApiUrl = "https://api.github.com"
  private val logger = Logger(this.getClass)
  private val authorizationToken = configuration.get[String]("gitHub.authorization.token")

  def searchCommits(authorName: String, beforeDays: Long): Future[JsValue] = {
    val qParameter = Seq(
      s"author:$authorName",
      s"author-date:>${beforeDayStringFromToday(beforeDays)}"
    ).mkString("+")
    // クエリパラメータは WSClient#withQueryStringParameters を使用すると記号がエンコードされてしまうため、直接 URL に含める。
    val request = ws.url(s"$baseApiUrl/search/commits?q=$qParameter")
      .withHttpHeaders(
        "Authorization" -> s"token $authorizationToken",
        "Accept" -> "application/vnd.github.cloak-preview"
      )
    logger.debug(request.url)
    request
      .get()
      .map(_.body[JsValue])
  }

  private def beforeDayFromToday(days: Long): LocalDate =
    LocalDate.now().minusDays(days)

  private def beforeDayStringFromToday(days: Long) =
    beforeDayFromToday(days).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

}
