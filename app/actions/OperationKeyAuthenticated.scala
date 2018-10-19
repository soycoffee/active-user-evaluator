package actions

import javax.inject.Inject
import play.api.Configuration
import play.api.mvc.{BodyParsers, RequestHeader}
import play.api.mvc.Security.AuthenticatedBuilder

import scala.concurrent.ExecutionContext

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
