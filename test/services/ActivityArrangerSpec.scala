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

  "sortByCreated, takeWhileBySince" in {
    val activityArranger = initializeTarget()
    val argActivities = Seq(
      Activity(null, LocalDateTime.of(1999, 12, 30, 23, 59, 59), null),
      Activity(null, LocalDateTime.of(1999, 12, 31, 0, 0, 0), null),
      Activity(null, LocalDateTime.of(1999, 12, 31, 0, 0, 1), null),
    )
    val returnActivities = activityArranger(argActivities, Some(1))
    returnActivities mustBe Seq(
      argActivities(2),
      argActivities(1),
    )
  }

  "defaultSinceBeforeDays" in {
    val activityArranger = initializeTarget()
    val argActivities = Seq(
      Activity(null, LocalDateTime.of(1999, 12, 24, 23, 59, 59), null),
      Activity(null, LocalDateTime.of(1999, 12, 25, 0, 0, 0), null),
    )
    val returnActivities = activityArranger(argActivities, None)
    returnActivities mustBe Seq(
      argActivities(1),
    )
  }

}
