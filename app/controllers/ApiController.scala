package controllers

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import javax.inject._
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiController @Inject()(implicit ws: WSClient, ec: ExecutionContext) extends InjectedController {

  def index: Action[AnyContent] = Action.async {
    getCommits("wesbos", 30)
      .map(Ok(_))
  }

  private def getCommits(committerName: String, beforeDays: Long): Future[JsValue] =
    ws.url("https://api.github.com/search/commits")
      .withQueryStringParameters("q" -> Seq(
        s"committer-name:$committerName",
        s"committer-date:>${beforeDayStringFromToday(beforeDays)}"
      ).mkString("+"))
      .withHttpHeaders(
        "Authorization" -> "token d7a2f421567a7a04587d3088301c341be6b9d394",
        "Accept" -> "application/vnd.github.cloak-preview"
      )
      .get()
      .map(_.body[JsValue])

  private def beforeDayFromToday(days: Long): LocalDate =
    LocalDate.now().minusDays(days)

  private def beforeDayStringFromToday(days: Long) =
    beforeDayFromToday(days).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

}
