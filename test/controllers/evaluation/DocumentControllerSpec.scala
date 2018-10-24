package controllers.evaluation

import models.Activity
import services._
import services.typetalk.WebhookResponseBodyBuilder

class DocumentControllerSpec extends UserControllerSpec[DocumentController] with TypetalkControllerSpec[DocumentController] {

  override protected def constructController: (UseApiDestination, EvaluationAggregator, WebhookResponseBodyBuilder) => DocumentController =
    new DocumentController(_, _, _)

  override protected val targetActivityTypes: Seq[Activity.Type] = BaseHelper.documentActivityTypes
  override protected val typetalkMessageLabel: String = BaseHelper.documentTypetalkMessageLabel

}
