package controllers.evaluation

import java.time.LocalDateTime

import models.{Activity, EvaluationActivity, EvaluationUser, User}
import org.mockito.Mockito
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import test.helpers.NoSlick

trait UserControllerSpec[Controller <: InjectedController with UserController] extends BaseSpec[Controller] with NoSlick {

  private def _initializeMock(evaluationUsers: Seq[EvaluationUser]) = {
    val (useApiDestination, evaluationAggregator) = super.initializeMock(evaluationUsers)
    val request = mock[Request[AnyContent]]
    (request, useApiDestination, evaluationAggregator)
  }

  "queryUsers" should {

    "OK" in  {
      val (request, useApiDestination, evaluationAggregator) = _initializeMock(Seq(
        EvaluationUser(
          User(1, "userId", "name"),
          Seq(
            EvaluationActivity(
              Activity(
                Activity.Type.CreateIssue,
                LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                Json.obj(
                  "key" -> "value",
                ),
              ),
              1,
            ),
          ),
        ),
      ))
      val controller = initializeTarget(useApiDestination, evaluationAggregator)
      val result = controller.queryUsers("projectId", Some(1), Some(1), "apiKey")(request)
      Helpers.status(result) mustBe Status.OK
      Helpers.contentAsJson(result) mustBe Json.arr(
        Json.obj(
          "id" -> 1,
          "userId" -> "userId",
          "name" -> "name",
          "point" -> 1,
          "activities" -> Json.arr(
            Json.obj(
              "type" -> "CreateIssue",
              "created" -> "2000-01-01T00:00:00",
              "point" -> 1,
            ),
          ),
        ),
      )
      Mockito.verify(evaluationAggregator).queryEvaluationUsers("projectId", targetActivityTypes, Some(1), Some(1))(apiDestination)
    }

  }

}
