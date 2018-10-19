package controllers

import controllers.ApiDefinitionController.OperationKeyAuthenticated
import javax.inject._
import models.ApiDefinition
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.Security.AuthenticatedBuilder
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
                                       )(implicit ec: ExecutionContext) extends InjectedController {

  apiDefinitionInitializer()

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

object ApiDefinitionController {

  class OperationKeyAuthenticated @Inject()(configuration: Configuration, parser: BodyParsers.Default)(implicit ec: ExecutionContext)
    extends AuthenticatedBuilder(
      OperationKeyAuthenticated.authenticate(configuration),
      parser,
    )

  object OperationKeyAuthenticated {

    val QueryStringKey = "operationKey"
    val ConfigurationKey = "apiDefinition.operationKey"

    def authenticate(configuration: Configuration)(request: RequestHeader): Option[String] = {
      val configurationOperationKey = configuration.get[String](ConfigurationKey)
      request.getQueryString(QueryStringKey).filter(_ == configurationOperationKey)
    }

  }

}
