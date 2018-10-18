package services

import java.time.LocalDateTime

import models.{Activity, User}
import org.mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.test.Helpers._

import scala.concurrent.{ExecutionContext, Future}

class BacklogApiClientSpec extends PlaySpec with GuiceOneServerPerSuite with MockitoSugar {

  override def fakeApplication(): Application =
    GuiceApplicationBuilder(
      overrides = Seq(
        bind[DatabaseConfigProvider].to(mock[DatabaseConfigProvider]),
      ),
    )
      .build()

  private implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  private implicit val destination: BacklogApiClient.Destination = BacklogApiClient.Destination("example.com", "key")

  private def initializeMock(body: JsValue) = {
    val wsClient = mock[WSClient]
    val wsRequest = mock[WSRequest]
    val wsResponse = mock[WSResponse]
    Mockito.when(wsClient.url(ArgumentMatchers.any())) thenReturn wsRequest
    Mockito.when(wsRequest.withMethod(ArgumentMatchers.any())) thenReturn wsRequest
    Mockito.when(wsRequest.execute()) thenReturn Future.successful(wsResponse)
    Mockito.when(wsResponse.body[JsValue](ArgumentMatchers.any())) thenReturn body
    (wsClient, wsRequest, wsResponse)
  }

  "queryProjectUsers" in {
    val (wsClient, wsRequest, _) = initializeMock(
      Json.arr(
        Json.obj(
          "id" -> 1,
          "userId" -> "userId",
          "name" -> "name",
        ),
      ),
    )
    val backlogApiClient = new BacklogApiClient(wsClient)
    val users = await(backlogApiClient.queryProjectUsers("1"))
    users mustBe Seq(
      User(1, "userId", "name"),
    )
    Mockito.verify(wsClient).url(ArgumentMatchers.eq("https://example.com/api/v2/projects/1/users?apiKey=key"))
    Mockito.verify(wsRequest).withMethod(ArgumentMatchers.eq("GET"))
  }

  "queryUserActivities" in {
    val (wsClient, wsRequest, _) = initializeMock(
      Json.arr(
        Json.obj(
          "type" -> 1,
          "created" -> "2000-01-01T00:00:00Z",
          "content" -> Json.obj(
            "key" -> "value",
          ),
        ),
      ),
    )
    val backlogApiClient = new BacklogApiClient(wsClient)
    val activities = await(backlogApiClient.queryUserActivities(1, Activity.Type.CreateIssue))
    activities mustBe Seq(
      Activity(
        Activity.Type.CreateIssue,
        LocalDateTime.of(2000, 1, 1, 0, 0, 0),
        Json.obj(
          "key" -> "value",
        ),
      ),
    )
    Mockito.verify(wsClient).url(ArgumentMatchers.eq("https://example.com/api/v2/users/1/activities?activityTypeId[]=1&count=100&apiKey=key"))
    Mockito.verify(wsRequest).withMethod(ArgumentMatchers.eq("GET"))
  }

}