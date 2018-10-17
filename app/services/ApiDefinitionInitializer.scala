package services

import repositories.ApiDefinitionRepository
import javax.inject.Inject
import models.ApiDefinition

trait ApiDefinitionInitializer {

  def apply(): Unit

}

object ApiDefinitionInitializer {

  class Dev @Inject()(apiDefinitionDao: ApiDefinitionRepository) extends ApiDefinitionInitializer {

    override def apply(): Unit =
      Seq(
        ApiDefinition("dev", "besnamfin.backlog.com", "LgEEkyZBDoToHZxD3kbmjWrr4nOWSWaiB98ZX4sYQvkvfszIQ2AJRwpokMRJIPTJ"),
      )
        .foreach(apiDefinitionDao.insert)

  }

  object Empty extends ApiDefinitionInitializer {

    override def apply(): Unit = ()

  }

}
