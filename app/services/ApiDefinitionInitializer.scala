package services

import dao.ApiDefinitionDao
import javax.inject.Inject
import models.ApiDefinition

trait ApiDefinitionInitializer {

  def apply(): Unit

}

object ApiDefinitionInitializer {

  class Dev @Inject()(apiDefinitionDao: ApiDefinitionDao) extends ApiDefinitionInitializer {

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
