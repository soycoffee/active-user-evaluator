package controllers.evaluation

import models.Activity
import services._
import services.typetalk.WebhookResponseBodyBuilder

class ImplementControllerSpec extends UserControllerSpec[ImplementController] with TypetalkControllerSpec[ImplementController] {

  override protected def constructController: (UseApiDestination, EvaluationAggregator, WebhookResponseBodyBuilder) => ImplementController =
    new ImplementController(_, _, _)

  override protected val targetActivityTypes: Seq[Activity.Type] = BaseHelper.implementActivityTypes
  override protected val typetalkMessageLabel: String = BaseHelper.implementTypetalkMessageLabel

}
