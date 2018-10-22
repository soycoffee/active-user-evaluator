package controllers.evaluation

import models.Activity
import services._

class ManagementControllerSpec extends JsonControllerSpec[ManagementController] with TypetalkControllerSpec[ManagementController] {

  import scala.concurrent.ExecutionContext.Implicits.global

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
