package services

import javax.inject.{Inject, Singleton}
import models._
import play.api.Configuration

@Singleton
class EvaluationUserArranger @Inject()(configuration: Configuration) {

  private val defaultCount: Int = configuration.get[Int]("evaluation.user.default.count")

  def apply(evaluationUsers: Seq[EvaluationUser], count: Option[Int]): Seq[EvaluationUser] =
    apply(evaluationUsers, count.getOrElse(defaultCount))

  def apply(evaluationUsers: Seq[EvaluationUser], count: Int): Seq[EvaluationUser] =
    filterByPositivePoint(takeByCount(sortByPoint(evaluationUsers), count))

  private def sortByPoint(evaluationUsers: Seq[EvaluationUser]): Seq[EvaluationUser] =
    evaluationUsers.sortBy(_.point * -1)

  private def takeByCount(evaluationUsers: Seq[EvaluationUser], count: Int): Seq[EvaluationUser] =
    evaluationUsers.take(count)

  private def filterByPositivePoint(evaluationUsers: Seq[EvaluationUser]): Seq[EvaluationUser] =
    evaluationUsers.filter(_.point > 0)

}

