package controllers.evaluation

import java.time.LocalDateTime

import models.{Activity, EvaluationActivity, EvaluationUser, User}
import org.mockito.{ArgumentMatchers, Mockito}
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.{JsObject, Json}
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

  def buildEvaluationUser(userId: Option[String], name: String, point: Int) =
    EvaluationUser(User(id = 0, userId = userId, name = name), Seq(EvaluationActivity(activity = null, point = point)))

  def buildEvaluationUser(id: Long, userId: Option[String], name: String, activities: Seq[EvaluationActivity]) =
    EvaluationUser(User(id = id, userId = userId, name = name), activities)

  def buildEvaluationActivity(`type`: Activity.Type, point: Int, created: LocalDateTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0), content: JsObject = Json.obj("key" -> "value")) =
    EvaluationActivity(Activity(`type` = `type`, projectKey = null, created = created, content = content), point)

}
