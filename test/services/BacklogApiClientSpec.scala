package services

import java.time.LocalDateTime

import models.{Activity, User}
import org.mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.test.Helpers._
import test.helpers.NoSlick

import scala.concurrent.Future

class BacklogApiClientSpec extends PlaySpec with GuiceOneServerPerSuite with MockitoSugar with NoSlick {

  import scala.concurrent.ExecutionContext.Implicits.global

  private implicit val destination: BacklogApiClient.Destination = BacklogApiClient.Destination("example.com", "key")

  private def initializeMock(body: JsValue) = {
    import ArgumentMatchers.any
    val wsClient = mock[WSClient]
    val wsRequest = mock[WSRequest]
    val wsResponse = mock[WSResponse]
    Mockito.when(wsClient.url(any())) thenReturn wsRequest
    Mockito.when(wsRequest.withMethod(any())) thenReturn wsRequest
    Mockito.when(wsRequest.execute()) thenReturn Future.successful(wsResponse)
    Mockito.when(wsResponse.body[JsValue](any())) thenReturn body
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
