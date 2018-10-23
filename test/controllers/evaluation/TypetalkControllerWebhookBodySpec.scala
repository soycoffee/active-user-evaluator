package controllers.evaluation

import controllers.evaluation.TypetalkController.WebhookBody
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class TypetalkControllerWebhookBodySpec extends PlaySpec {

  private def buildJsObject(message: String, replyFrom: Long) =
    Json.obj(
      "post" -> Json.obj(
        "message" -> message,
        "account" -> Json.obj(
          "id" -> replyFrom,
        ),
      )
    )

  "reads" should {

    "message.empty" in {
      WebhookBody.reads.reads(buildJsObject("", 1)) mustBe JsSuccess(WebhookBody(None, None, 1))
    }

    "message.nonEmpty" in {
      WebhookBody.reads.reads(buildJsObject("count=1 sinceBeforeDays=2", 3)) mustBe JsSuccess(WebhookBody(Some(1), Some(2), 3))
    }

  }

}
