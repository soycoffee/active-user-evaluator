package controllers.evaluation

import models.{Activity, EvaluationUser}
import org.mockito.{ArgumentMatchers, Mockito}
import org.scalatest.mockito.MockitoSugar
import play.api.mvc._
import services.{BacklogApiClient, EvaluationAggregator, UseApiDestination}

import scala.concurrent.Future

object BaseHelper extends MockitoSugar {

  implicit val apiDestination: BacklogApiClient.Destination = BacklogApiClient.Destination("example.com", "")

  val managementActivityTypes = Seq(
    Activity.Type.CreateIssue,
    Activity.Type.UpdateIssue,
    Activity.Type.CreateIssueComment,
    Activity.Type.UpdateMultiIssue,
    Activity.Type.CreateVersion,
    Activity.Type.UpdateVersion,
  )

  val documentActivityTypes = Seq(
    Activity.Type.CreateWiki,
    Activity.Type.UpdateWiki,
    Activity.Type.CreateFile,
    Activity.Type.UpdateFile,
  )

  val implementActivityTypes = Seq(
    Activity.Type.CreateGitPush,
    Activity.Type.CreateGitRepository,
    Activity.Type.CreatePullRequest,
    Activity.Type.UpdatePullRequest,
    Activity.Type.CreatePullRequestComment,
  )

  val managementTypetalkMessageLabel = "# マネジメント系アクティビティ ( 課題 / マイルストーン )"
  val documentTypetalkMessageLabel = "# ドキュメント系アクティビティ ( Wiki / ファイル )"
  val implementTypetalkMessageLabel = "# 実装系アクティビティ ( Git )"

  def initializeMock(evaluationUsers: Seq[EvaluationUser]): (UseApiDestination, EvaluationAggregator) = {
    import ArgumentMatchers.any
    val useApiDestination = mock[UseApiDestination]
    val evaluationAggregator = mock[EvaluationAggregator]
    Mockito.when(useApiDestination(any())(any())) thenAnswer (_.getArgument[BacklogApiClient.Destination => Future[Result]](1)(apiDestination))
    Mockito.when(evaluationAggregator.queryEvaluationUsers(any(), any(), any(), any())(any())) thenReturn Future.successful(evaluationUsers)
    (useApiDestination, evaluationAggregator)
  }

}
