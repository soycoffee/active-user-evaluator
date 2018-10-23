package controllers

import actions.OperationKeyAuthenticated
import javax.inject._
import models.ApiDefinition
import play.api.libs.json.Json
import play.api.mvc._
import repositories.ApiDefinitionRepository
import services.{ApiDefinitionInitializer, ApiDefinitionKeyGenerator}

import scala.concurrent.ExecutionContext

@Singleton
class ApiDefinitionController @Inject()(
                                         parse: PlayBodyParsers,
                                         apiDefinitionDao: ApiDefinitionRepository,
                                         apiDefinitionInitializer: ApiDefinitionInitializer,
                                         apiDefinitionKeyGenerator: ApiDefinitionKeyGenerator,
                                         operationKeyAuthenticated: OperationKeyAuthenticated,
                                       ) extends InjectedController {

  apiDefinitionInitializer()

  implicit lazy val ec: ExecutionContext = defaultExecutionContext

  def query: Action[AnyContent] = operationKeyAuthenticated.async {
    apiDefinitionDao.all()
      .map(Json.toJson(_))
      .map(Ok(_))
  }

  def create: Action[ApiDefinition] = operationKeyAuthenticated.async(parse.json(apiDefinitionKeyGenerator.reads)) { request =>
    apiDefinitionDao.insert(request.body)
      .map(Json.toJson(_))
      .map(Ok(_))
  }

  def update: Action[ApiDefinition] = operationKeyAuthenticated.async(parse.json(ApiDefinition.format)) { request =>
    apiDefinitionDao.update(request.body)
      .map(_.map(Json.toJson(_)))
      .map(_.fold[Result](NotFound)(Ok(_)))
  }

  def delete(key: String): Action[AnyContent] = operationKeyAuthenticated.async {
    apiDefinitionDao.deleteByKey(key)
      .map({
        case true => NoContent
        case false => NotFound
      })
  }

}
