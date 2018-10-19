//package services
//
//import java.time.LocalDateTime
//
//import models.{Activity, User}
//import org.mockito._
//import org.scalatest.mockito.MockitoSugar
//import org.scalatestplus.play.PlaySpec
//import org.scalatestplus.play.guice.GuiceOneServerPerSuite
//import play.api.Application
//import play.api.db.slick.DatabaseConfigProvider
//import play.api.inject.bind
//import play.api.inject.guice.GuiceApplicationBuilder
//import play.api.libs.json.{JsValue, Json}
//import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
//import play.api.test.Helpers._
//
//import scala.concurrent.{ExecutionContext, Future}
//
//class EvaluationAggregatorSpec extends PlaySpec with GuiceOneServerPerSuite with MockitoSugar {
//
//  override def fakeApplication(): Application =
//    GuiceApplicationBuilder(
//      overrides = Seq(
//        bind[DatabaseConfigProvider].to(mock[DatabaseConfigProvider]),
//      ),
//    )
//      .build()
//
//  private implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
//  private implicit val destination: BacklogApiClient.Destination = BacklogApiClient.Destination("example.com", "key")
//
//  private def initializeMock(users: Seq[User], activities: Seq[Activity]) = {
//    import ArgumentMatchers.any
//    val backlogApiClient = mock[BacklogApiClient]
//    Mockito.when(backlogApiClient.queryProjectUsers(any())) thenReturn Future.successful(users)
//    Mockito.when(backlogApiClient.queryUserActivities(any(), any())) thenReturn Future.successful(activities)
//    backlogApiClient
//  }
//
//  private def initializeTarget(backlogApiClient: BacklogApiClient) =
//    new EvaluationAggregator(
//      backlogApiClient,
//      app.injector.instanceOf[ActivityPointJudge],
//      app.injector.instanceOf[ActivityArranger],
//    )
//
//  "queryProjectUsers" in {
//    val backlogApiClient = initializeMock(
//      Seq(),
//      Seq(),
//    )
//    val evaluationAggregator = initializeTarget(backlogApiClient)
//    evaluationAggregator.queryEvaluationUsers(1, Some())
//
//    val users = await(backlogApiClient.queryProjectUsers("1"))
//    users mustBe Seq(
//      User(1, "userId", "name"),
//    )
//    Mockito.verify(wsClient).url(ArgumentMatchers.eq("https://example.com/api/v2/projects/1/users?apiKey=key"))
//    Mockito.verify(wsRequest).withMethod(ArgumentMatchers.eq("GET"))
//  }
//
//}
