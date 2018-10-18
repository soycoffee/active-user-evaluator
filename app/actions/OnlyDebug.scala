package actions

import com.google.inject.Inject
import play.api.{Environment, Mode}
import play.api.mvc._
import Results.NotFound
import javax.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OnlyDebug @Inject()(parser: BodyParsers.Default, environment: Environment)(implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    if (Seq(Mode.Dev, Mode.Test) contains environment.mode)
      super.invokeBlock(request, block)
    else
      Future.successful(NotFound)
  }

}

