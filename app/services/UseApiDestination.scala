package services

import javax.inject.Inject
import play.api.mvc.Result
import play.api.mvc.Results.NotFound
import repositories.ApiDefinitionRepository

import scala.concurrent.{ExecutionContext, Future}

class UseApiDestination @Inject()(apiDefinitionRepository: ApiDefinitionRepository)(implicit ec: ExecutionContext) {

  def apply(apiKey: String)(f: BacklogApiAccessor.Destination => Future[Result]): Future[Result] = {
    apiDefinitionRepository.findByKey(apiKey).map({
      case Some(apiDefinition) => f(BacklogApiAccessor.Destination(apiDefinition.backlogDomain, apiDefinition.backlogApiKey))
      case None => Future.successful(NotFound)
    }).flatten
  }

}
