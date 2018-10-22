package controllers.evaluation

import models.Activity
import services._

class DocumentControllerSpec extends JsonControllerSpec {

  import scala.concurrent.ExecutionContext.Implicits.global

  override protected val constructController: (UseApiDestination, EvaluationAggregator) => JsonController =
    new DocumentController(_, _)

  override protected val targetActivityTypes: Seq[Activity.Type] = Seq(
    Activity.Type.CreateWiki,
    Activity.Type.UpdateWiki,
    Activity.Type.CreateFile,
    Activity.Type.UpdateFile,
  )

}
