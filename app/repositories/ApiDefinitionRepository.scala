package repositories

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.{Inject, Singleton}
import models.ApiDefinition
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

@Singleton
class ApiDefinitionRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val ApiDefinitions = TableQuery[ApiDefinitionsTable]

  def all(): Future[Seq[ApiDefinition]] =
    db.run(ApiDefinitions.result)

  def findByKey(key: String): Future[Option[ApiDefinition]] =
    db.run(ApiDefinitions.filter(_.key === key).result.headOption)

  def insert(apiDefinition: ApiDefinition): Future[ApiDefinition] =
    db.run(ApiDefinitions += apiDefinition).map(_ => apiDefinition)

  def update(apiDefinition: ApiDefinition): Future[Option[ApiDefinition]] =
    db.run(ApiDefinitions
      .filter(_.key === apiDefinition.key)
      .map(x => (x.backlog_domain, x.backlog_api_key))
      .update((apiDefinition.backlogDomain, apiDefinition.backlogApiKey))
    ) map {
      case 0 => None
      case _ => Some(apiDefinition)
    }

  def deleteByKey(key: String): Future[Boolean] =
    db.run(ApiDefinitions.filter(_.key === key).delete).map(_ != 0)

  private class ApiDefinitionsTable(tag: Tag) extends Table[ApiDefinition](tag, "api_definitions") {

    def key = column[String]("key", O.PrimaryKey)
    def backlog_domain = column[String]("backlog_domain")
    def backlog_api_key = column[String]("backlog_api_key")

    def * = (key, backlog_domain, backlog_api_key) <> ((ApiDefinition.apply _).tupled, ApiDefinition.unapply)

  }

}