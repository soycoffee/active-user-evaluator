package services

import javax.inject.{Inject, Singleton}
import models._
import play.api.Configuration

import scala.Function.tupled
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EvaluationAggregator @Inject()(
                                      configuration: Configuration,
                                      backlogApiClient: BacklogApiClient,
                                      activityPointAggregator: ActivityPointJudge,
                                      activityArranger: ActivityArranger,
                                      evaluationUserArranger: EvaluationUserArranger,
                                      futureSerializer: FutureSerializer,
                                    )(implicit val ec: ExecutionContext) {

  private implicit val backlogApiInterval: Option[FiniteDuration] = configuration.getOptional[FiniteDuration]("backlog.api.interval")

  def queryEvaluationUsers(activityTypes: Seq[Activity.Type], projectKey: String, count: Option[Int], sinceBeforeDays: Option[Int])(implicit destination: BacklogApiClient.Destination): Future[Seq[EvaluationUser]] =
    for {
      users <- queryUsers(projectKey)
      usersActivities <- queryUsersActivities(activityTypes, users.map(_.id))
    } yield {
      evaluationUserArranger(
        (users zip usersActivities
            .map(activityArranger(_, projectKey, sinceBeforeDays))
        ).map(tupled(evaluate)),
        count,
      )
    }

  private def queryUsers(projectKey: String)(implicit destination: BacklogApiClient.Destination): Future[Seq[User]] =
    backlogApiClient.queryProjectUsers(projectKey)

  private def queryUsersActivities(activityTypes: Seq[Activity.Type], userIds: Seq[Long])(implicit destination: BacklogApiClient.Destination): Future[Seq[Seq[Activity]]] =
    futureSerializer[Long, Seq[Activity]](userIds)(queryUserActivitiesByTypes(activityTypes, _))

  private def queryUserActivitiesByTypes(activityTypes: Seq[Activity.Type], userId: Long)(implicit destination: BacklogApiClient.Destination): Future[Seq[Activity]] =
    futureSerializer[Activity.Type, Seq[Activity]](activityTypes)(backlogApiClient.queryUserActivities(userId, _))
      .map(_.flatten)

  private def evaluate(user: User, activities: Seq[Activity]) =
    EvaluationUser(
      user,
      (activities zip activities.map(activityPointAggregator(_)))
        .map(tupled(EvaluationActivity.apply)),
    )

}


