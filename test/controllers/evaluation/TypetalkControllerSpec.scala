package controllers.evaluation

import models.{EvaluationActivity, EvaluationUser, User}
import org.mockito.Mockito
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import test.helpers.NoSlick

trait TypetalkControllerSpec[Controller <: TypetalkController with InjectedController] extends BaseSpec[Controller] with NoSlick {

  private def initializeMock(requestBody: JsValue, evaluationUsers: Seq[EvaluationUser]) = {
    val (useApiDestination, evaluationAggregator) = super.initializeMock(evaluationUsers)
    val request = mock[Request[JsValue]]
    Mockito.when(request.body) thenReturn requestBody
    (request, useApiDestination, evaluationAggregator)
  }

  private def buildRequestBody(postMessage: String, replyFrom: Long) =
    Json.obj(
      "post" -> Json.obj(
        "message" -> postMessage,
        "account" -> Json.obj(
          "id" -> replyFrom,
        ),
      )
    )

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
        buildRequestBody("", 1),
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
            |${controller.typetalkMessageLabel}
            |1. [A](https://example.com/user/a) ( 3 points )
            |2. [B](https://example.com/user/b) ( 2 points )
            |3. [C](https://example.com/user/c) ( 1 points )
          """.stripMargin.trim,
        "replyTo" -> 1,
      )
      Mockito.verify(evaluationAggregator).queryEvaluationUsers("projectId", targetActivityTypes, None, None)(apiDestination)
    }

    "parseMessage" in {
      val (request, useApiDestination, evaluationAggregator) = initializeMock(
        buildRequestBody("count=1 sinceBeforeDays=2", 1),
        Seq(),
      )
      val controller = initializeTarget(useApiDestination, evaluationAggregator)
      val result = controller.typetalkWebhook("projectId", "apiKey")(request)
      Helpers.status(result) mustBe Status.OK
      Mockito.verify(evaluationAggregator).queryEvaluationUsers("projectId", targetActivityTypes, Some(1), Some(2))(apiDestination)
    }

  }

}
