package controllers

import com.typesafe.config.ConfigFactory
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.Call
import play.api.test.Helpers._
import play.api.{Application, Configuration}
import services.ApiDefinitionInitializer

class ApiDefinitionControllerSpec extends PlaySpec with GuiceOneServerPerSuite {

  override def fakeApplication(): Application =
    GuiceApplicationBuilder(
      configuration = Configuration(ConfigFactory.parseResources("dev.conf")),
      overrides = Seq(
        bind[ApiDefinitionInitializer].to[ApiDefinitionInitializer.Dev],
      ),
    )
      .build()

  private implicit val wsClient: WSClient = app.injector.instanceOf[WSClient]

  private def authenticated(call: Call) =
    wsCall(call)
      .withQueryStringParameters("operationKey" -> "dev")

  "Unauthorized" should {

    for(call <- Seq(
      routes.ApiDefinitionController.query(),
      routes.ApiDefinitionController.create(),
    )) {

      s"${call.method} ${call.url}" in {
        val response = await(
          wsCall(call)
            .withQueryStringParameters("operationKey" -> "invalid")
            .execute(),
        )
        response.status mustBe UNAUTHORIZED
      }

    }

  }

  "query" should {

    "OK" in  {
      val response = await(authenticated(routes.ApiDefinitionController.query()).execute())
      response.status mustBe OK
      response.json mustBe Json.arr(
        Json.obj(
          "key" -> "dev",
          "backlogDomain" -> "besnamfin.backlog.com",
          "backlogApiKey" -> "LgEEkyZBDoToHZxD3kbmjWrr4nOWSWaiB98ZX4sYQvkvfszIQ2AJRwpokMRJIPTJ",
        ),
      )
    }

  }

  "create" should {

    "OK" in  {
      val requestBody = Json.obj(
        "key" -> "key",
        "backlogDomain" -> "backlogDomain",
        "backlogApiKey" -> "backlogApiKey",
      )
      val response = await(authenticated(routes.ApiDefinitionController.create()).post(requestBody))
      response.status mustBe OK
      response.json mustBe requestBody
      val queryResponse = await(authenticated(routes.ApiDefinitionController.query()).execute())
      val queryObjects = queryResponse.json.as[Seq[JsValue]]
      queryObjects must have length 2
      queryObjects.last mustBe requestBody
    }

  }

}
