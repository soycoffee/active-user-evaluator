package controllers

import controllers.ApiDefinitionController.OperationKeyAuthenticated
import dao.ApiDefinitionDao
import javax.inject._
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.Security.AuthenticatedBuilder
import play.api.mvc._
import services.ApiDefinitionInitializer

import scala.concurrent.ExecutionContext

@Singleton
class ApiDefinitionController @Inject()(
                                         apiDefinitionDao: ApiDefinitionDao,
                                         apiDefinitionInitializer: ApiDefinitionInitializer,
                                         operationKeyAuthenticated: OperationKeyAuthenticated,
                                       )(implicit ec: ExecutionContext) extends InjectedController {

  apiDefinitionInitializer()

  def index: Action[AnyContent] = operationKeyAuthenticated.async {
    apiDefinitionDao.all()
      .map(Json.toJson(_))
      .map(Ok(_))
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
