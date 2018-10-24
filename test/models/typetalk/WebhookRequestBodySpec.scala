package models.typetalk

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class WebhookRequestBodySpec extends PlaySpec {

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
      WebhookRequestBody.reads.reads(buildJsObject("", 1)) mustBe JsSuccess(WebhookRequestBody(None, None, 1))
    }

    "message.nonEmpty" in {
      WebhookRequestBody.reads.reads(buildJsObject("count=1 sinceBeforeDays=2", 3)) mustBe JsSuccess(WebhookRequestBody(Some(1), Some(2), 3))
    }

  }

}
