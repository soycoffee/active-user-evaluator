package services

import models.{Activity, User}
import org.mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.{Application, Configuration}

import scala.concurrent.{ExecutionContext, Future}

class EvaluationAggregatorSpec extends PlaySpec with GuiceOneServerPerSuite with MockitoSugar {

  override def fakeApplication(): Application =
    GuiceApplicationBuilder(
      overrides = Seq(
        bind[DatabaseConfigProvider].to(mock[DatabaseConfigProvider]),
      ),
    )
      .build()

  private implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  private implicit val destination: BacklogApiClient.Destination = BacklogApiClient.Destination("example.com", "key")

  private val noActivityArranger = new ActivityArranger(
    app.injector.instanceOf[Configuration],
    null,
  ) {

    override def apply(activities: Seq[Activity], sinceBeforeDays: Option[Int]): Seq[Activity] =
      activities

  }

  private def initializeMock(users: Seq[User], activities: Seq[Activity]) = {
    import ArgumentMatchers.any
    val backlogApiClient = mock[BacklogApiClient]
    Mockito.when(backlogApiClient.queryProjectUsers(any())(any())) thenReturn Future.successful(users)
    Mockito.when(backlogApiClient.queryUserActivities(any(), any())(any())) thenReturn Future.successful(activities)
    backlogApiClient
  }

  private def initializeTarget(backlogApiClient: BacklogApiClient) =
    new EvaluationAggregator(
      backlogApiClient,
      app.injector.instanceOf[ActivityPointJudge],
      noActivityArranger,
    )

  "queryEvaluationUsers" in {
    val mockUsers = Seq(
      User(1, null, null),
      User(2, null, null),
    )
    val mockActivities = Seq(
      Activity(Activity.Type.CreateIssue, null, null),
      Activity(Activity.Type.UpdateIssue, null, null),
      Activity(Activity.Type.CreateIssueComment, null, null),
    )
    val argActivityTypes = Seq(
      Activity.Type.CreateWiki,
      Activity.Type.UpdateWiki,
      Activity.Type.CreateFile,
      Activity.Type.UpdateFile,
    )
    val backlogApiClient = initializeMock(mockUsers, mockActivities)
    val evaluationAggregator = initializeTarget(backlogApiClient)
    val evaluationUsers = await(evaluationAggregator.queryEvaluationUsers("1", None, argActivityTypes))
    evaluationUsers must have length 2
    evaluationUsers.map(_.user) mustBe mockUsers
    evaluationUsers.map(_.point) mustBe Seq.fill(2)(12)
    evaluationUsers.map(_.activities) foreach { evaluationActivities =>
      evaluationActivities must have length 12
      evaluationActivities.map(_.activity) mustBe Seq.fill(4)(mockActivities).flatten
      evaluationActivities.map(_.point) mustBe Seq.fill(12)(1)
    }
    Mockito.verify(backlogApiClient)
      .queryProjectUsers(ArgumentMatchers.eq("1"))(ArgumentMatchers.eq(destination))
    Mockito.verify(backlogApiClient, Mockito.times(8))
      .queryUserActivities(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.eq(destination))
    mockUsers foreach { mockUser =>
      argActivityTypes foreach { argActivityType =>
        Mockito.verify(backlogApiClient)
          .queryUserActivities(ArgumentMatchers.eq(mockUser.id), ArgumentMatchers.eq(argActivityType))(ArgumentMatchers.eq(destination))
      }
    }
  }

  "queryEvaluationUsers users = Nil" in {
    val mockUsers = Nil
    val mockActivities = Seq(
      Activity(Activity.Type.CreateIssue, null, null),
    )
    val argActivityTypes = Seq(
      Activity.Type.CreateWiki,
    )
    val backlogApiClient = initializeMock(mockUsers, mockActivities)
    val evaluationAggregator = initializeTarget(backlogApiClient)
    val evaluationUsers = await(evaluationAggregator.queryEvaluationUsers("1", None, argActivityTypes))
    evaluationUsers mustBe Nil
    Mockito.verify(backlogApiClient, Mockito.never())
      .queryUserActivities(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.eq(destination))
  }

  "queryEvaluationUsers activityTypes = Nil" in {
    val mockUsers = Seq(
      User(1, null, null),
    )
    val mockActivities = Seq(
      Activity(Activity.Type.CreateIssue, null, null),
    )
    val argActivityTypes = Nil
    val backlogApiClient = initializeMock(mockUsers, mockActivities)
    val evaluationAggregator = initializeTarget(backlogApiClient)
    val evaluationUsers = await(evaluationAggregator.queryEvaluationUsers("1", None, argActivityTypes))
    evaluationUsers must have length 1
    evaluationUsers.head.user mustBe mockUsers.head
    evaluationUsers.head.point mustBe 0
    evaluationUsers.head.activities mustBe Nil
    Mockito.verify(backlogApiClient, Mockito.never())
      .queryUserActivities(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.eq(destination))
  }

  "queryEvaluationUsers activityTypes = Seq(CreateGitPush)" in {
    val mockUsers = Seq(
      User(1, null, null),
    )
    val mockActivities = Seq(
      Activity(Activity.Type.CreateGitPush, null, Json.obj(
        "revision_count" -> 2,
      )),
    )
    val argActivityTypes = Seq(Activity.Type.CreateGitPush)
    val backlogApiClient = initializeMock(mockUsers, mockActivities)
    val evaluationAggregator = initializeTarget(backlogApiClient)
    val evaluationUsers = await(evaluationAggregator.queryEvaluationUsers("1", None, argActivityTypes))
    evaluationUsers must have length 1
    evaluationUsers.head.point mustBe 2
  }

}
