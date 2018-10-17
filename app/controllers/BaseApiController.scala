package controllers

import play.api.mvc.{Action, AnyContent, InjectedController}
import services.{BacklogApiAccessor, UseApiDestination}

import scala.concurrent.Future

trait BaseApiController extends InjectedController {

  val useApiDestination: UseApiDestination
  val backlogApiAccessor: BacklogApiAccessor

  def index(projectId: String, apiKey: String): Action[AnyContent] = Action.async {
    useApiDestination(apiKey) { implicit destination =>
//      backlogApiAccessor.queryProjectUsers(projectId).map({ users =>
//        users.map(backlogApiAccessor.queryUsersActivitiesAsJson())
//      })
      Future.successful(Ok("ok"))
    }
  }

}
