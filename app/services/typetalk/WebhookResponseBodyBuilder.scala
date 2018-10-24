package services.typetalk

import javax.inject.Inject
import models._
import services.BacklogApiClient

import scala.Function.tupled
import scala.concurrent.ExecutionContext

class WebhookResponseBodyBuilder @Inject()(implicit ec: ExecutionContext) {

  def apply(label: String, evaluationUsers: Seq[EvaluationUser])(implicit destination: BacklogApiClient.Destination): String =
    (evaluationUsers zip Stream.from(1)).map(tupled((evaluationUser, order) =>
      s"$order. [${evaluationUser.name}](https://${destination.domain}/user/${evaluationUser.userId}) ( ${evaluationUser.point} points )"
    ))
      .+:(label)
      .mkString("\n")

}




