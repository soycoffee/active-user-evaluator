package controllers.evaluation

import models.{Activity, EvaluationUser}
import org.mockito.Mockito
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import services.typetalk.WebhookResponseBodyBuilder
import services.{EvaluationAggregator, UseApiDestination}
import test.helpers.NoSlick

trait UserControllerSpec[Controller <: InjectedController with UserController] extends PlaySpec with GuiceOneServerPerSuite with MockitoSugar with NoSlick {

  import BaseHelper.apiDestination

  protected val targetActivityTypes: Seq[Activity.Type]

  protected def constructController: (UseApiDestination, EvaluationAggregator, WebhookResponseBodyBuilder) => Controller

  private def initializeMock(evaluationUsers: Seq[EvaluationUser]) = {
    val (useApiDestination, evaluationAggregator) = BaseHelper.initializeMock(evaluationUsers)
    val request = mock[Request[AnyContent]]
    (request, useApiDestination, evaluationAggregator)
  }

  private def initializeTarget(useApiDestination: UseApiDestination, evaluationAggregator: EvaluationAggregator): Controller = {
    val controller = constructController(useApiDestination, evaluationAggregator, null)
    controller.setControllerComponents(app.injector.instanceOf[ControllerComponents])
    controller
  }

  "queryUsers" should {

    "OK" in  {
      val (request, useApiDestination, evaluationAggregator) = initializeMock(Seq(
        BaseHelper.buildEvaluationUser(1, Some("userId1"), "name1", Seq(BaseHelper.buildEvaluationActivity(Activity.Type.CreateIssue, 1))),
        BaseHelper.buildEvaluationUser(2, None, "name2", Seq(BaseHelper.buildEvaluationActivity(Activity.Type.UpdateIssue, 2))),
      ))
      val controller = initializeTarget(useApiDestination, evaluationAggregator)
      val result = controller.queryUsers("projectKey", Some(1), Some(1), "apiKey")(request)
      Helpers.status(result) mustBe Status.OK
      Helpers.contentAsJson(result) mustBe Json.arr(
        Json.obj(
          "id" -> 1,
          "userId" -> "userId1",
          "name" -> "name1",
          "point" -> 1,
          "activities" -> Json.arr(
            Json.obj(
              "type" -> "CreateIssue",
              "created" -> "2000-01-01T00:00:00",
              "point" -> 1,
            ),
          ),
        ),
        Json.obj(
          "id" -> 2,
          "name" -> "name2",
          "point" -> 2,
          "activities" -> Json.arr(
            Json.obj(
              "type" -> "UpdateIssue",
              "created" -> "2000-01-01T00:00:00",
              "point" -> 2,
            ),
          ),
        ),
      )
      Mockito.verify(evaluationAggregator).queryEvaluationUsers(targetActivityTypes, "projectKey", Some(1), Some(1))(apiDestination)
    }

  }

}
