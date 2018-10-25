package services

import akka.actor.ActorSystem
import akka.pattern.after
import javax.inject.{Inject, Singleton}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FutureSerializer @Inject()(actorSystem: ActorSystem)(implicit val ec: ExecutionContext) {

  def apply[Source, Result](sources: Seq[Source])(f: Source => Future[Result])(implicit interval: Option[FiniteDuration]): Future[Seq[Result]] =
    sources.foldLeft[Future[Seq[Result]]](Future.successful(Nil)) { (results$, source) =>
      for {
        results <- results$
        result <- beforeDelay(f(source))
      } yield results :+ result
    }

  private def beforeDelay[T](f: => Future[T])(implicit interval: Option[FiniteDuration]): Future[T] =
    after(interval.getOrElse(0.second), actorSystem.scheduler)(f)

}


