package services

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

class ApiDefinitionKeyGeneratorSpec extends PlaySpec with GuiceOneServerPerSuite with MockitoSugar {

  override def fakeApplication(): Application =
    GuiceApplicationBuilder(
      overrides = Seq(
        bind[DatabaseConfigProvider].to(mock[DatabaseConfigProvider]),
      ),
    )
      .build()

  private val UuidRegex = "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}".r

  "apply" in {
    val apiDefinitionKeyGenerator = app.injector.instanceOf[ApiDefinitionKeyGenerator]
    apiDefinitionKeyGenerator() must fullyMatch regex UuidRegex
    apiDefinitionKeyGenerator() must not be apiDefinitionKeyGenerator()
  }

}
