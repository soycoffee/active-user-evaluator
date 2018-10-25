package services

import java.time.{LocalDate, LocalDateTime}

import models.Activity
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import test.helpers.NoSlick

class ActivityArrangerSpec extends PlaySpec with GuiceOneServerPerSuite with NoSlick {

  private val fixedLocalDateNowProvider = new ActivityArranger.LocalDateNowProvider {

    override def apply(): LocalDate = LocalDate.of(2000, 1, 1)

  }

  private def initializeTarget() =
    new ActivityArranger(
      app.configuration,
      fixedLocalDateNowProvider,
    )

  "filterByProjectKey" in {
    val activityArranger = initializeTarget()
    val created = LocalDateTime.MAX
    val argActivities = Seq(
      Activity(null, "projectKey", created, null),
      Activity(null, "invalid", created, null),
    )
    val returnActivities = activityArranger(argActivities, "projectKey", Some(1))
    returnActivities mustBe Seq(
      argActivities(0),
    )
  }

  "sortByCreated, takeWhileBySince" in {
    val activityArranger = initializeTarget()
    val argActivities = Seq(
      Activity(null, "projectKey", LocalDateTime.of(1999, 12, 30, 23, 59, 59), null),
      Activity(null, "projectKey", LocalDateTime.of(1999, 12, 31, 0, 0, 0), null),
      Activity(null, "projectKey", LocalDateTime.of(1999, 12, 31, 0, 0, 1), null),
    )
    val returnActivities = activityArranger(argActivities, "projectKey", Some(1))
    returnActivities mustBe Seq(
      argActivities(2),
      argActivities(1),
    )
  }

  "defaultSinceBeforeDays" in {
    val activityArranger = initializeTarget()
    val argActivities = Seq(
      Activity(null, "projectKey", LocalDateTime.of(1999, 12, 24, 23, 59, 59), null),
      Activity(null, "projectKey", LocalDateTime.of(1999, 12, 25, 0, 0, 0), null),
    )
    val returnActivities = activityArranger(argActivities, "projectKey", None)
    returnActivities mustBe Seq(
      argActivities(1),
    )
  }

}
