package controllers

import play.api.mvc.{Action, AnyContent, InjectedController}
import services.{BacklogApiClient, UseApiDestination}

import scala.concurrent.Future

trait BaseApiController extends InjectedController {

  val useApiDestination: UseApiDestination
  val backlogApiClient: BacklogApiClient

  def index(projectId: String, apiKey: String): Action[AnyContent] = Action.async {
    useApiDestination(apiKey) { implicit destination =>
//      backlogApiClient.queryProjectUsers(projectId).map({ users =>
//        users.map(backlogApiClient.queryUsersActivitiesAsJson())
//      })
      Future.successful(Ok("ok"))
    }
  }

}
