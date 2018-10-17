package controllers

import dao.ApiDefinitionDao
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class ApiDefinitionController @Inject()(apiDefinitionDao: ApiDefinitionDao)(implicit ec: ExecutionContext) extends InjectedController {

  def index: Action[AnyContent] = Action.async {
    apiDefinitionDao.all()
      .map(Json.toJson(_))
      .map(Ok(_))
  }

}
