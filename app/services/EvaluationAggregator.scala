package services

import javax.inject.{Inject, Singleton}
import models._

import scala.Function.tupled
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EvaluationAggregator @Inject()(
                                      backlogApiClient: BacklogApiClient,
                                      activityPointAggregator: ActivityPointJudge,
                                      activityArranger: ActivityArranger,
                                      evaluationUserArranger: EvaluationUserArranger,
                                    )(implicit val ec: ExecutionContext) {

  def queryEvaluationUsers(projectId: String, activityTypes: Seq[Activity.Type], count: Option[Int], sinceBeforeDays: Option[Int])(implicit destination: BacklogApiClient.Destination): Future[Seq[EvaluationUser]] =
    for {
      users <- queryUsers(projectId)
      usersActivities <- queryUsersActivities(users.map(_.id), activityTypes)
    } yield {
      evaluationUserArranger(
        (users zip usersActivities
            .map(activityArranger(_, sinceBeforeDays))
        ).map(tupled(evaluate)),
        count,
      )
    }

  private def queryUsers(projectId: String)(implicit destination: BacklogApiClient.Destination): Future[Seq[User]] =
    backlogApiClient.queryProjectUsers(projectId)

  private def queryUsersActivities(userIds: Seq[Long], activityTypes: Seq[Activity.Type])(implicit destination: BacklogApiClient.Destination): Future[Seq[Seq[Activity]]] =
    Future.sequence(userIds.map({
      queryUserActivitiesByTypes(_, activityTypes)
    }))

  private def queryUserActivitiesByTypes(userId: Long, activityTypes: Seq[Activity.Type])(implicit destination: BacklogApiClient.Destination): Future[Seq[Activity]] =
    Future.sequence(activityTypes.map({
      backlogApiClient.queryUserActivities(userId, _)
    }))
      .map(_.flatten)

  private def evaluate(user: User, activities: Seq[Activity]) =
    EvaluationUser(
      user,
      (activities zip activities.map(activityPointAggregator(_)))
        .map(tupled(EvaluationActivity.apply)),
    )

}


