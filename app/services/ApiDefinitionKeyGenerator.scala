package services

import java.util.UUID

import com.google.inject.ImplementedBy
import javax.inject.Singleton
import models.ApiDefinition
import play.api.libs.json.{Json, Reads}

@ImplementedBy(classOf[ApiDefinitionKeyGenerator.ByUuid])
trait ApiDefinitionKeyGenerator {

  def apply(): String

  def reads: Reads[ApiDefinition] =
    ApiDefinition.format
      .compose(Reads.JsObjectReads.map(_ ++ Json.obj(
        "key" -> apply(),
      )))

}

object ApiDefinitionKeyGenerator {

  @Singleton
  class ByUuid extends ApiDefinitionKeyGenerator {

    def apply(): String =
      UUID.randomUUID().toString

  }

}
