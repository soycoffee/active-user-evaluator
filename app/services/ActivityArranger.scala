package services

import java.time.{LocalDate, LocalDateTime}

import javax.inject.{Inject, Singleton}
import models._
import play.api.Configuration
import services.ActivityArranger.LocalDateNowProvider

@Singleton
class ActivityArranger @Inject()(
                                  configuration: Configuration,
                                  localDateNowProvider: LocalDateNowProvider,
                                ) {

  import ActivityArranger._

  private val defaultSinceBeforeDays: Int = configuration.get[Int]("evaluation.activity.default.sinceBeforeDays")

  def apply(activities: Seq[Activity], projectKey: String, sinceBeforeDays: Option[Int]): Seq[Activity] =
    apply(activities, projectKey, sinceBeforeDays.getOrElse(defaultSinceBeforeDays))

  def apply(activities: Seq[Activity], projectKey: String, sinceBeforeDays: Int): Seq[Activity] =
    apply(activities, projectKey, localDateNowProvider().minusDays(sinceBeforeDays).atTime(0, 0, 0))

  def apply(activities: Seq[Activity], projectKey: String, sinceDateTime: LocalDateTime): Seq[Activity] =
    takeWhileBySince(sortByCreated(filterByProjectKey(activities, projectKey)), sinceDateTime)

  private def filterByProjectKey(activities: Seq[Activity], projectKey: String): Seq[Activity] =
    activities.filter(_.projectKey == projectKey)

  private def sortByCreated(activities: Seq[Activity]): Seq[Activity] =
    activities.sortBy(_.created)(localDateTimeOrdering.reverse)

  private def takeWhileBySince(activities: Seq[Activity], sinceDateTime: LocalDateTime): Seq[Activity] =
    activities.takeWhile(activity => (activity.created isAfter sinceDateTime) || (activity.created isEqual sinceDateTime))

}

object ActivityArranger {

  private val localDateTimeOrdering: Ordering[LocalDateTime] =
    (x: LocalDateTime, y: LocalDateTime) => x compareTo y

  class LocalDateNowProvider {

    def apply(): LocalDate = LocalDate.now()

  }

}