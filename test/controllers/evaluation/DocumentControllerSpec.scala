package controllers.evaluation

import models.Activity
import services._

class DocumentControllerSpec extends UserControllerSpec[DocumentController] with TypetalkControllerSpec[DocumentController] {

  override protected val constructController: (UseApiDestination, EvaluationAggregator) => DocumentController =
    new DocumentController(_, _)

  override protected val targetActivityTypes: Seq[Activity.Type] = Seq(
    Activity.Type.CreateWiki,
    Activity.Type.UpdateWiki,
    Activity.Type.CreateFile,
    Activity.Type.UpdateFile,
  )

}
