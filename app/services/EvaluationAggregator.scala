package services

import akka.actor.ActorSystem
import akka.pattern.after
import javax.inject.{Inject, Singleton}
import models._
import play.api.Configuration

import scala.Function.tupled
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EvaluationAggregator @Inject()(
                                      configuration: Configuration,
                                      actorSystem: ActorSystem,
                                      backlogApiClient: BacklogApiClient,
                                      activityPointAggregator: ActivityPointJudge,
                                      activityArranger: ActivityArranger,
                                      evaluationUserArranger: EvaluationUserArranger,
                                    )(implicit val ec: ExecutionContext) {

  private val backlogApiInterval = configuration.get[FiniteDuration]("backlog.api.interval")

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
    userIds.foldLeft(Future.successful(Nil: Seq[Seq[Activity]]))({ (groupedActivities$, userId) =>
      for {
        groupedActivities <- groupedActivities$
        activities <- queryUserActivitiesByTypes(activityTypes, userId)
      } yield groupedActivities :+ activities
    })

  private def queryUserActivitiesByTypes(activityTypes: Seq[Activity.Type], userId: Long)(implicit destination: BacklogApiClient.Destination): Future[Seq[Activity]] =
    // 直列に実行するため、 foldLeft を用いる。
    // 並列に実行すると、 API にアクセスを制限されてしまう。
    activityTypes.foldLeft(Future.successful(Nil: Seq[Activity]))({ (activities$, activityType) =>
      for {
        activitiesA <- activities$
        activitiesB <- beforeDelay(backlogApiClient.queryUserActivities(userId, activityType))
      } yield activitiesA ++ activitiesB
    })

  private def evaluate(user: User, activities: Seq[Activity]) =
    EvaluationUser(
      user,
      (activities zip activities.map(activityPointAggregator(_)))
        .map(tupled(EvaluationActivity.apply)),
    )

  private def beforeDelay[T](f: => Future[T]): Future[T] =
    after(backlogApiInterval, actorSystem.scheduler)(f)

}


