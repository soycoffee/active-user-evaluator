package controllers

import javax.inject._
import play.api.mvc._
import services.GitHubSearcher

import scala.concurrent.ExecutionContext

@Singleton
class ApiController @Inject()(implicit gitHubSearcher: GitHubSearcher, ec: ExecutionContext) extends InjectedController {

  def index(names: String, beforeDays: Long): Action[AnyContent] = Action.async {
    gitHubSearcher.searchCommits(names, beforeDays)
      .map(Ok(_))
  }

}
