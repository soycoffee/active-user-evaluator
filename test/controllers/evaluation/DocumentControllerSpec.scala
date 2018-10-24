package controllers.evaluation

import models.Activity
import services._

class DocumentControllerSpec extends UserControllerSpec[DocumentController] with TypetalkControllerSpec[DocumentController] {

  override protected def constructController: (UseApiDestination, EvaluationAggregator, TypetalkMessageBuilder) => DocumentController =
    new DocumentController(_, _, _)

  override protected val targetActivityTypes: Seq[Activity.Type] = Seq(
    Activity.Type.CreateWiki,
    Activity.Type.UpdateWiki,
    Activity.Type.CreateFile,
    Activity.Type.UpdateFile,
  )

  override protected val typetalkMessageLabel: String = "ドキュメント系アクティビティ ( Wiki / ファイル )"

}
