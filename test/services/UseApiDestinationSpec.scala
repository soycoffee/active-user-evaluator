package services

import com.typesafe.config.ConfigFactory
import models.ApiDefinition
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Results
import play.api.test.Helpers._
import play.api.{Application, Configuration}
import repositories.ApiDefinitionRepository

import scala.concurrent.Future

class UseApiDestinationSpec extends PlaySpec with GuiceOneServerPerSuite with MockitoSugar {

  override def fakeApplication(): Application =
    GuiceApplicationBuilder(
      configuration = Configuration(ConfigFactory.parseResources("dev.conf")),
    )
      .build()

  private val apiDefinitionRepository = app.injector.instanceOf[ApiDefinitionRepository]

  private def insertApiDefinition(apiDefinition: ApiDefinition) =
    await(apiDefinitionRepository.insert(apiDefinition))

  "apply" should {

    "OK" in {
      insertApiDefinition(ApiDefinition("ok", "backlogDomain", "backlogApiKey"))
      val useApiDestination = app.injector.instanceOf[UseApiDestination]
      var passedApiDestination: BacklogApiClient.Destination = null
      val result = await(useApiDestination("ok") { apiDestination =>
        passedApiDestination = apiDestination
        Future.successful(Results.Ok)
      })
      result mustBe Results.Ok
      passedApiDestination mustBe BacklogApiClient.Destination("backlogDomain", "backlogApiKey")
    }

    "NOT_FOUND" in {
      val useApiDestination = app.injector.instanceOf[UseApiDestination]
      val result = await(useApiDestination("notExistsKey") { _ =>
        fail()
      })
      result mustBe Results.NotFound
    }

  }

}
