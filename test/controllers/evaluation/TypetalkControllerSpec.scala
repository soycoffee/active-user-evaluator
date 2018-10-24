package controllers.evaluation

import models.typetalk.WebhookRequestBody
import models.{Activity, EvaluationActivity, EvaluationUser, User}
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

trait TypetalkControllerSpec[Controller <: TypetalkController with InjectedController] extends PlaySpec with GuiceOneServerPerSuite with MockitoSugar with NoSlick {

  import BaseHelper.apiDestination

  protected val targetActivityTypes: Seq[Activity.Type]
  protected val typetalkMessageLabel: String

  protected def constructController: (UseApiDestination, EvaluationAggregator, WebhookResponseBodyBuilder) => Controller

  private def initializeMock(requestBody: WebhookRequestBody, evaluationUsers: Seq[EvaluationUser]) = {
    val (useApiDestination, evaluationAggregator) = BaseHelper.initializeMock(evaluationUsers)
    val request = mock[Request[WebhookRequestBody]]
    Mockito.when(request.body) thenReturn requestBody
    Mockito.when(request.contentType) thenReturn Some("application/json")
    (request, useApiDestination, evaluationAggregator)
  }

  private def initializeTarget(useApiDestination: UseApiDestination, evaluationAggregator: EvaluationAggregator): Controller = {
    val controller = constructController(useApiDestination, evaluationAggregator, new WebhookResponseBodyBuilder())
    controller.setControllerComponents(app.injector.instanceOf[ControllerComponents])
    controller
  }

  "typetalkWebhook" should {

    def buildEvaluationUser(userId: String, name: String, point: Int) =
      EvaluationUser(
        User(0, userId = userId, name = name),
        Seq(
          EvaluationActivity(null, point = point),
        ),
      )

    "OK" in {
      val (request, useApiDestination, evaluationAggregator) = initializeMock(
        WebhookRequestBody("", 1),
        Seq(
          buildEvaluationUser("a", "A", 3),
          buildEvaluationUser("b", "B", 2),
          buildEvaluationUser("c", "C", 1),
        ),
      )
      val controller = initializeTarget(useApiDestination, evaluationAggregator)
      val result = controller.typetalkWebhook("projectId", "apiKey")(request)
      Helpers.status(result) mustBe Status.OK
      Helpers.contentAsJson(result) mustBe Json.obj(
        "message" ->
          s"""|
            |----------------
            |$typetalkMessageLabel
            |1. [A](https://example.com/user/a) ( 3 points )
            |2. [B](https://example.com/user/b) ( 2 points )
            |3. [C](https://example.com/user/c) ( 1 points )
            |----------------
          """.stripMargin.trim,
        "replyTo" -> 1,
      )
      Mockito.verify(evaluationAggregator).queryEvaluationUsers(targetActivityTypes, "projectId", None, None)
    }

  }

}
