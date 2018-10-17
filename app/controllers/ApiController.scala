package controllers

import javax.inject._
import play.api.mvc._
import services.BacklogApiClient

import scala.concurrent.ExecutionContext

@Singleton
class ApiController @Inject()(backlogApiClient: BacklogApiClient)(implicit ec: ExecutionContext) extends InjectedController {

  def index() = Action {
    Ok("ok")
  }

}
