package controllers.evaluation

import models.{Activity, EvaluationUser}
import org.mockito.{ArgumentMatchers, Mockito}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.mvc._
import services.{BacklogApiClient, EvaluationAggregator, TypetalkMessageBuilder, UseApiDestination}

import scala.concurrent.Future

trait BaseSpec[Controller <: InjectedController] extends PlaySpec with GuiceOneServerPerSuite with MockitoSugar {

  protected def constructController: (UseApiDestination, EvaluationAggregator, TypetalkMessageBuilder) => Controller

  protected val targetActivityTypes: Seq[Activity.Type]

  protected implicit val apiDestination: BacklogApiClient.Destination = BacklogApiClient.Destination("example.com", "")

  protected def initializeMock(evaluationUsers: Seq[EvaluationUser]): (UseApiDestination, EvaluationAggregator) = {
    import ArgumentMatchers.any
    val useApiDestination = mock[UseApiDestination]
    val evaluationAggregator = mock[EvaluationAggregator]
    Mockito.when(useApiDestination(any())(any())) thenAnswer (_.getArgument[BacklogApiClient.Destination => Future[Result]](1)(apiDestination))
    Mockito.when(evaluationAggregator.queryEvaluationUsers(any(), any(), any(), any())(any())) thenReturn Future.successful(evaluationUsers)
    (useApiDestination, evaluationAggregator)
  }

  protected def initializeTarget(useApiDestination: UseApiDestination, evaluationAggregator: EvaluationAggregator): Controller = {
    val typetalkMessageBuilder = new TypetalkMessageBuilder()(scala.concurrent.ExecutionContext.global)
    val controller = constructController(useApiDestination, evaluationAggregator, typetalkMessageBuilder)
    controller.setControllerComponents(app.injector.instanceOf[ControllerComponents])
    controller
  }

}
