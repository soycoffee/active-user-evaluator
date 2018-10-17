package controllers.debug

import actions.OnlyDebug
import javax.inject._
import play.api.mvc._
import repositories.ApiDefinitionRepository
import services.BacklogApiAccessor

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BacklogController @Inject()(onlyDebug: OnlyDebug, backlogApiAccessor: BacklogApiAccessor, apiDefinitionRepository: ApiDefinitionRepository)(implicit ec: ExecutionContext) extends InjectedController {

  def queryUsers(projectId: String, apiKey: String): Action[AnyContent] = onlyDebug.async {
    useApiDestination(apiKey) { implicit apiDefinition =>
      backlogApiAccessor.queryProjectUsersAsJson(projectId)
        .map(Ok(_))
    }
  }

  def queryActivities(userId: Long, apiKey: String): Action[AnyContent] = onlyDebug.async {
    useApiDestination(apiKey) { implicit apiDefinition =>
      backlogApiAccessor.queryUsersActivitiesAsJson(userId)
        .map(Ok(_))
    }
  }

  private def useApiDestination(apiKey: String)(f: BacklogApiAccessor.Destination => Future[Result]): Future[Result] = {
    apiDefinitionRepository.findByKey(apiKey).map({
      case Some(apiDefinition) => f(BacklogApiAccessor.Destination(apiDefinition.backlogDomain, apiDefinition.backlogApiKey))
      case None => Future.successful(NotFound)
    }).flatten
  }

}
