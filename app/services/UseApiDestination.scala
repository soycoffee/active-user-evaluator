package services

import javax.inject.Inject
import play.api.mvc.Result
import play.api.mvc.Results.NotFound
import repositories.ApiDefinitionRepository

import scala.concurrent.{ExecutionContext, Future}

class UseApiDestination @Inject()(apiDefinitionRepository: ApiDefinitionRepository)(implicit ec: ExecutionContext) {

  def apply(apiKey: String)(f: BacklogApiClient.Destination => Future[Result]): Future[Result] = {
    apiDefinitionRepository.findByKey(apiKey).map({
      case Some(apiDefinition) => f(BacklogApiClient.Destination(apiDefinition.backlogDomain, apiDefinition.backlogApiKey))
      case None => Future.successful(NotFound)
    }).flatten
  }

}
