package services

import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

import javax.inject._
import play.api.Logger
import play.api.libs.Files.TemporaryFileCreator

import scala.util.Try

@Singleton
class GitCommandExecutor @Inject()(temporaryFileCreator: TemporaryFileCreator) {

  import GitCommandExecutor._

  import sys.process._

  val logger = Logger(this.getClass)

  def temporaryFetch(httpUrl: String): Try[Path] = {
    val tempDirectoryPath = temporaryFileCreator.create().path.getParent
    val repositoryPath = tempDirectoryPath.resolve(UUID.randomUUID().toString)
    execute(
      (baseCommand(tempDirectoryPath) ++ Seq("clone", "--bare", httpUrl, repositoryPath.getFileName.toString)) #&&
      (baseCommand(repositoryPath) ++ Seq("fetch", "--all"))
    )
      .map(_ => repositoryPath)
  }

  def readCommitsCount(repositoryPath: Path, author: String, sinceBeforeDays: Int): Try[Int] =
    execute(
      baseCommand(repositoryPath) ++ Seq("rev-list", "--count", "--all") ++ countOptions(author, sinceBeforeDays)
    )
      .map(_.trim.toInt)

  def readChangesCountSummary(repositoryPath: Path, author: String, sinceBeforeDays: Int): Try[ChangesCountSummary] =
    execute(
      (baseCommand(repositoryPath) ++ Seq("log", "--numstat", "--pretty=\"%H\"") ++ countOptions(author, sinceBeforeDays)) #|
      "awk 'NF==3 {plus+=$1; minus+=$2} END {printf(\"%d %d\", plus, minus)}'"
    )
      .map(_.trim.split(" ") match {
        case Array(addition: String, deletion: String) => ChangesCountSummary(addition.toInt, deletion.toInt)
      })

  private def execute(commands: ProcessBuilder) = {
    logger.debug(s"Execute: $commands")
    Try(commands.!!)
  }

  private def baseCommand(directoryPath: Path) =
    Seq("git", "-C", s"""$directoryPath""")

  private def countOptions(author: String, sinceBeforeDays: Int) =
    Seq("--no-merges", "--branches" , "--author", s"""$author""", "--since", beforeDayStringFromToday(sinceBeforeDays))

}

object GitCommandExecutor {

  case class ChangesCountSummary(addition: Int, deletion: Int)

  private def beforeDayFromToday(days: Int): LocalDate =
    LocalDate.now().minusDays(days)

  private def beforeDayStringFromToday(days: Int) =
    beforeDayFromToday(days).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

}
