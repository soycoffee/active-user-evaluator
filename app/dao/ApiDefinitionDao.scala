package dao

import scala.concurrent.{ ExecutionContext, Future }
import javax.inject.Inject

import models.ApiDefinition
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

class ApiDefinitionDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val ApiDefinitions = TableQuery[ApiDefinitionsTable]

  def all(): Future[Seq[ApiDefinition]] = db.run(ApiDefinitions.result)

  def insert(apiDefinition: ApiDefinition): Future[Unit] = db.run(ApiDefinitions += apiDefinition).map { _ => () }

  private class ApiDefinitionsTable(tag: Tag) extends Table[ApiDefinition](tag, "API_DEFINITIONS") {

    def key = column[String]("KEY", O.PrimaryKey)
    def backlog_domain = column[String]("BACKLOG_DOMAIN")
    def backlog_api_key = column[String]("BACKLOG_API_KEY")

    def * = (key, backlog_domain, backlog_api_key) <> ((ApiDefinition.apply _).tupled, ApiDefinition.unapply)

  }

}