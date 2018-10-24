package controllers.evaluation

import models.Activity
import services._
import services.typetalk.WebhookResponseBodyBuilder

class ManagementControllerSpec extends UserControllerSpec[ManagementController] with TypetalkControllerSpec[ManagementController] {

  override protected def constructController: (UseApiDestination, EvaluationAggregator, WebhookResponseBodyBuilder) => ManagementController =
    new ManagementController(_, _, _)

  override protected val targetActivityTypes: Seq[Activity.Type] = BaseHelper.managementActivityTypes
  override protected val typetalkMessageLabel: String = BaseHelper.managementTypetalkMessageLabel

}
