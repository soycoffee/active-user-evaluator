package controllers.evaluation

import models.EvaluationUser
import org.mockito.{ArgumentMatchers, Mockito}
import org.scalatest.mockito.MockitoSugar
import play.api.mvc._
import services.{BacklogApiClient, EvaluationAggregator, UseApiDestination}

import scala.concurrent.Future

object BaseHelper extends MockitoSugar {

  implicit val apiDestination: BacklogApiClient.Destination = BacklogApiClient.Destination("example.com", "")

  def initializeMock(evaluationUsers: Seq[EvaluationUser]): (UseApiDestination, EvaluationAggregator) = {
    import ArgumentMatchers.any
    val useApiDestination = mock[UseApiDestination]
    val evaluationAggregator = mock[EvaluationAggregator]
    Mockito.when(useApiDestination(any())(any())) thenAnswer (_.getArgument[BacklogApiClient.Destination => Future[Result]](1)(apiDestination))
    Mockito.when(evaluationAggregator.queryEvaluationUsers(any(), any(), any(), any())(any())) thenReturn Future.successful(evaluationUsers)
    (useApiDestination, evaluationAggregator)
  }

}
