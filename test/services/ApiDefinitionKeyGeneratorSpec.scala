package services

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import test.helpers.NoSlick

class ApiDefinitionKeyGeneratorSpec extends PlaySpec with GuiceOneServerPerSuite with NoSlick {

  private val UuidRegex = "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}".r

  "ByUuid" should {

    "apply" in {
      val apiDefinitionKeyGenerator = new ApiDefinitionKeyGenerator.ByUuid
      apiDefinitionKeyGenerator() must fullyMatch regex UuidRegex
      apiDefinitionKeyGenerator() must not be apiDefinitionKeyGenerator()
    }

  }

}
