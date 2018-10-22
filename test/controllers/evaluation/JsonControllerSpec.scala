package controllers.evaluation

import java.time.LocalDateTime

import models.{Activity, EvaluationActivity, EvaluationUser, User}
import org.mockito.{ArgumentMatchers, Mockito}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import services._
import test.helpers.NoSlick

import scala.concurrent.Future

trait JsonControllerSpec extends PlaySpec with GuiceOneServerPerSuite with MockitoSugar with NoSlick {

  protected val constructController: (UseApiDestination, EvaluationAggregator) => JsonController

  protected val targetActivityTypes: Seq[Activity.Type]

  private implicit val apiDestination: BacklogApiClient.Destination = BacklogApiClient.Destination("", "")

  private def initializeMock(evaluationUsers: Seq[EvaluationUser]) = {
    import ArgumentMatchers.any
    val request = mock[Request[AnyContent]]
    val useApiDestination = mock[UseApiDestination]
    val evaluationAggregator = mock[EvaluationAggregator]
    Mockito.when(useApiDestination(any())(any())) thenAnswer (_.getArgument[BacklogApiClient.Destination => Future[Result]](1)(apiDestination))
    Mockito.when(evaluationAggregator.queryEvaluationUsers(any(), any(), any(), any())(any())) thenReturn Future.successful(evaluationUsers)
    (request, useApiDestination, evaluationAggregator)
  }

  private def initializeTarget(useApiDestination: UseApiDestination, evaluationAggregator: EvaluationAggregator) = {
    val controller = constructController(useApiDestination, evaluationAggregator)
    controller.asInstanceOf[InjectedController].setControllerComponents(app.injector.instanceOf[ControllerComponents])
    controller
  }

  "index" should {

    "OK" in  {
      val (request, useApiDestination, evaluationAggregator) = initializeMock(Seq(
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
      val result = controller.index("projectId", Some(1), Some(1), "apiKey")(request)
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
