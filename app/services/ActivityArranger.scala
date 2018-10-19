package services

import java.time.{LocalDate, LocalDateTime}

import javax.inject.{Inject, Singleton}
import models._
import play.api.Configuration

import scala.concurrent.ExecutionContext

@Singleton
class ActivityArranger @Inject()(
                                  configuration: Configuration,
                                  useApiDestination: UseApiDestination,
                                  backlogApiClient: BacklogApiClient,
                                  activityPointAggregator: ActivityPointJudge,
                                )(implicit val ec: ExecutionContext) {

  import ActivityArranger._

  private val defaultSinceBeforeDays: Int = configuration.get[Int]("evaluation.default.sinceBeforeDays")

  def apply(activities: Seq[Activity], sinceBeforeDays: Option[Int]): Seq[Activity] =
    apply(activities, sinceBeforeDays.getOrElse(defaultSinceBeforeDays))

  def apply(activities: Seq[Activity], sinceBeforeDays: Int): Seq[Activity] =
    apply(activities, LocalDate.now().minusDays(sinceBeforeDays).atStartOfDay())

  def apply(activities: Seq[Activity], sinceDateTime: LocalDateTime): Seq[Activity] =
    takeWhileBySince(sort(activities), sinceDateTime)

  private def sort(activities: Seq[Activity]): Seq[Activity] =
    activities.sortBy(_.created)(localDateTimeOrdering)

  private def takeWhileBySince(activities: Seq[Activity], sinceDateTime: LocalDateTime): Seq[Activity] =
    activities.takeWhile(activity => (activity.created isAfter sinceDateTime) || (activity.created isEqual sinceDateTime))

}

object ActivityArranger {

  private val localDateTimeOrdering: Ordering[LocalDateTime] =
    (x: LocalDateTime, y: LocalDateTime) => x compareTo y

}