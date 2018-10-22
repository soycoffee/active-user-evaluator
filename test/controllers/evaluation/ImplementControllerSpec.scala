package controllers.evaluation

import models.Activity
import services._

class ImplementControllerSpec extends JsonControllerSpec[ImplementController] with TypetalkControllerSpec[ImplementController] {

  import scala.concurrent.ExecutionContext.Implicits.global

  override protected val constructController: (UseApiDestination, EvaluationAggregator) => ImplementController =
    new ImplementController(_, _)

  override protected val targetActivityTypes: Seq[Activity.Type] = Seq(
    Activity.Type.CreateGitPush,
    Activity.Type.CreateGitRepository,
    Activity.Type.CreatePullRequest,
    Activity.Type.UpdatePullRequest,
    Activity.Type.CreatePullRequestComment,
  )

}
