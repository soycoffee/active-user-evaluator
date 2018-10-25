package services

import models.{EvaluationActivity, EvaluationUser, User}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import test.helpers.NoSlick

class EvaluationUserArrangerSpec extends PlaySpec with GuiceOneServerPerSuite with NoSlick {

  private def initializeTarget() =
    new EvaluationUserArranger(
      app.configuration,
    )

  "filterByPositivePoint" in {
    val evaluationUserArranger = initializeTarget()
    val argEvaluationUsers = Seq(
      EvaluationUser(User(1, null, null), Seq(EvaluationActivity(null, 1))),
      EvaluationUser(User(2, null, null), Seq(EvaluationActivity(null, 0))),
    )
    val returnEvaluationUsers = evaluationUserArranger(argEvaluationUsers, Some(3))
    returnEvaluationUsers mustBe Seq(
      argEvaluationUsers(0),
    )
  }

  "sortByPoint, takeByCount" in {
    val evaluationUserArranger = initializeTarget()
    val argEvaluationUsers = Seq(
      EvaluationUser(User(1, null, null), Seq()),
      EvaluationUser(User(2, null, null), Seq(EvaluationActivity(null, 1))),
      EvaluationUser(User(3, null, null), Seq(EvaluationActivity(null, 2))),
    )
    val returnEvaluationUsers = evaluationUserArranger(argEvaluationUsers, Some(2))
    returnEvaluationUsers mustBe Seq(
      argEvaluationUsers(2),
      argEvaluationUsers(1),
    )
  }

  "defaultCount" in {
    val evaluationUserArranger = initializeTarget()
    val argEvaluationUsers = Seq(
      EvaluationUser(User(1, null, null), Seq(EvaluationActivity(null, 1))),
      EvaluationUser(User(2, null, null), Seq(EvaluationActivity(null, 1))),
      EvaluationUser(User(3, null, null), Seq(EvaluationActivity(null, 1))),
      EvaluationUser(User(4, null, null), Seq(EvaluationActivity(null, 1))),
    )
    val returnEvaluationUsers = evaluationUserArranger(argEvaluationUsers, None)
    returnEvaluationUsers mustBe Seq(
      argEvaluationUsers(0),
      argEvaluationUsers(1),
      argEvaluationUsers(2),
    )
  }

}
