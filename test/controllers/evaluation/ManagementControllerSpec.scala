package controllers.evaluation

import models.Activity
import services._

class ManagementControllerSpec extends UserControllerSpec[ManagementController] with TypetalkControllerSpec[ManagementController] {

  override protected def constructController: (UseApiDestination, EvaluationAggregator, TypetalkMessageBuilder) => ManagementController =
    new ManagementController(_, _, _)

  override protected val targetActivityTypes: Seq[Activity.Type] = Seq(
    Activity.Type.CreateIssue,
    Activity.Type.UpdateIssue,
    Activity.Type.CreateIssueComment,
    Activity.Type.UpdateMultiIssue,
    Activity.Type.CreateVersion,
    Activity.Type.UpdateVersion,
  )

  override protected val typetalkMessageLabel: String = "マネジメント系アクティビティ ( 課題 / マイルストーン )"

}
