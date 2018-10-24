package models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class TypetalkWebhookBodySpec extends PlaySpec {

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
      TypetalkWebhookBody.reads.reads(buildJsObject("", 1)) mustBe JsSuccess(TypetalkWebhookBody(None, None, 1))
    }

    "message.nonEmpty" in {
      TypetalkWebhookBody.reads.reads(buildJsObject("count=1 sinceBeforeDays=2", 3)) mustBe JsSuccess(TypetalkWebhookBody(Some(1), Some(2), 3))
    }

  }

}
