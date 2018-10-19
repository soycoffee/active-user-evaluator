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
import services.{ApiDefinitionInitializer, ApiDefinitionKeyGenerator}

class ApiDefinitionControllerSpec extends PlaySpec with GuiceOneServerPerSuite {

  private val fixedApiDefinitionKeyGenerator = new ApiDefinitionKeyGenerator {

    override def apply(): String = "fixedKey"

  }

  override def fakeApplication(): Application =
    GuiceApplicationBuilder(
      configuration = Configuration(ConfigFactory.parseResources("dev.conf")),
      overrides = Seq(
        bind[ApiDefinitionInitializer].to[ApiDefinitionInitializer.Dev],
        bind[ApiDefinitionKeyGenerator].toInstance(fixedApiDefinitionKeyGenerator),
      ),
    )
      .build()

  private implicit def wsClient: WSClient = app.injector.instanceOf[WSClient]

  private def authenticated(call: Call) =
    wsCall(call)
      .withQueryStringParameters("operationKey" -> "dev")

  "UNAUTHORIZED" should {

    for(call <- Seq(
      routes.ApiDefinitionController.query(),
      routes.ApiDefinitionController.create(),
      routes.ApiDefinitionController.update(),
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
        "backlogDomain" -> "createBacklogDomain",
        "backlogApiKey" -> "createBacklogApiKey",
      )
      val response = await(authenticated(routes.ApiDefinitionController.create()).post(requestBody))
      response.status mustBe OK
      val requestBodyWithKey = requestBody ++ Json.obj(
        "key" -> "fixedKey",
      )
      response.json mustBe requestBodyWithKey
      val queryResponse = await(authenticated(routes.ApiDefinitionController.query()).execute())
      val queryObjects = queryResponse.json.as[Seq[JsValue]]
      queryObjects must have length 2
      queryObjects.last mustBe requestBodyWithKey
    }

  }

  "update" should {

    "OK" in  {
      val requestBody = Json.obj(
        "key" -> "fixedKey",
        "backlogDomain" -> "updateBacklogDomain",
        "backlogApiKey" -> "updateBacklogApiKey",
      )
      val response = await(authenticated(routes.ApiDefinitionController.update()).put(requestBody))
      response.status mustBe OK
      response.json mustBe requestBody
      val queryResponse = await(authenticated(routes.ApiDefinitionController.query()).execute())
      val queryObjects = queryResponse.json.as[Seq[JsValue]]
      queryObjects.last mustBe requestBody
    }

    "NOT_FOUND" in  {
      val requestBody = Json.obj(
        "key" -> "notExistsKey",
        "backlogDomain" -> "",
        "backlogApiKey" -> "",
      )
      val response = await(authenticated(routes.ApiDefinitionController.update()).put(requestBody))
      response.status mustBe NOT_FOUND
    }

  }

  "delete" should {

    "NO_CONTENT" in  {
      val requestKey = "fixedKey"
      val response = await(authenticated(routes.ApiDefinitionController.delete(requestKey)).delete())
      response.status mustBe NO_CONTENT
      val queryResponse = await(authenticated(routes.ApiDefinitionController.query()).execute())
      val restKeys = queryResponse.json.as[Seq[JsValue]].map(_("key").as[String])
      restKeys mustBe Seq("dev")
    }

    "NOT_FOUND" in  {
      val requestKey = "notExistsKey"
      val response = await(authenticated(routes.ApiDefinitionController.delete(requestKey)).delete())
      response.status mustBe NOT_FOUND
    }

  }

}
