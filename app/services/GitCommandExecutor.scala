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
class GitCommandExecutor @Inject()(implicit temporaryFileCreator: TemporaryFileCreator) {

  import GitCommandExecutor._

  import sys.process._

  val logger = Logger(this.getClass)

  def temporaryFetch(httpUrl: String): Try[Path] = {
    val tempDirectoryPath = temporaryFileCreator.create().path.getParent
    val repositoryPath = tempDirectoryPath.resolve(UUID.randomUUID().toString)
    val commands =
      s"""${baseCommand(tempDirectoryPath)} clone --bare $httpUrl ${repositoryPath.getFileName}""" #&&
      s"""${baseCommand(repositoryPath)} fetch --all""""
    logger.debug(s"Execute: $commands")
    Try(commands.!)
      .map(_ => repositoryPath)
  }

  def readCommitsCount(repositoryPath: Path, author: String, sinceBeforeDays: Int): Try[Int] = {
    val commands =
      s"""${baseCommand(repositoryPath)} rev-list --count --all --no-merges --branches --author="$author" --since=${beforeDayStringFromToday(sinceBeforeDays)}"""
    logger.debug(s"Execute: $commands")
    Try(commands.!!)
      .map(_.trim.toInt)
  }

  def readChangesCountSummary(repositoryPath: Path, author: String, sinceBeforeDays: Int): Try[ChangesCountSummary] = {
    val commands =
      s"""${baseCommand(repositoryPath)} log --numstat --pretty="%H" --no-merges --branches  --author="$author" --since=${beforeDayStringFromToday(sinceBeforeDays)}""" #|
      "awk 'NF==3 {plus+=$1; minus+=$2} END {printf(\"%d %d\", plus, minus)}'"
    logger.debug(s"Execute: $commands")
    Try(commands.!!)
      .map(_.trim.split(" ") match {
        case Array(addition: String, deletion: String) => ChangesCountSummary(addition.toInt, deletion.toInt)
      })
  }

  private def baseCommand(directoryPath: Path) =
    s"""git -C "$directoryPath""""

}

object GitCommandExecutor {

  case class ChangesCountSummary(addition: Int, deletion: Int)

  private def beforeDayFromToday(days: Int): LocalDate =
    LocalDate.now().minusDays(days)

  private def beforeDayStringFromToday(days: Int) =
    beforeDayFromToday(days).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

}
