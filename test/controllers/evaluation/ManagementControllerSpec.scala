package controllers.evaluation

import models.Activity
import services._

class ManagementControllerSpec extends UserControllerSpec[ManagementController] with TypetalkControllerSpec[ManagementController] {

  override protected val constructController: (UseApiDestination, EvaluationAggregator) => ManagementController =
    new ManagementController(_, _)

  override protected val targetActivityTypes: Seq[Activity.Type] = Seq(
    Activity.Type.CreateIssue,
    Activity.Type.UpdateIssue,
    Activity.Type.CreateIssueComment,
    Activity.Type.UpdateMultiIssue,
    Activity.Type.CreateVersion,
    Activity.Type.UpdateVersion,
  )

}
