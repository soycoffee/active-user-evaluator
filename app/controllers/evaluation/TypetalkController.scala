package controllers.evaluation

import models._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc._
import services.{BacklogApiClient, EvaluationAggregator, UseApiDestination}

import scala.Function.tupled
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait TypetalkController extends BaseController with HasTargetActivityTypes {

  val useApiDestination: UseApiDestination
  val evaluationAggregator: EvaluationAggregator
  implicit val ec: ExecutionContext

  def typetalkMessageLabel: String

  def typetalkWebhook(projectId: String, apiKey: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withRequestParameter { (postMessage, replyFrom) =>
      val (count, sinceBeforeDays) = extractMessageParameter(postMessage)
      useApiDestination(apiKey) { implicit destination =>
        evaluationAggregator.queryEvaluationUsers(projectId, targetActivityTypes, count, sinceBeforeDays)
          .map(buildResponseBody(_, replyFrom))
          .map(Json.toJson(_))
          .map(Ok(_))
      }
    } recoverTotal { error =>
      Future.successful(BadRequest(error.toString))
    }
  }

  private def withRequestParameter[T](f: (String, Long) => T)(implicit request: Request[JsValue]) = {
    for {
      postMessage <- (request.body \ "post" \ "message").validate[String]
      replyFrom <- (request.body \ "post" \ "account" \ "id").validate[Long]
    } yield f(postMessage, replyFrom)
  }

  private def extractMessageParameter[T](message: String) = {
    val pickInt = (key: String) =>
      s"""$key=\\S+""".r.findFirstIn(message).flatMap(Try(_).map(_.split("=")(1).toInt).toOption)
    (pickInt("count"), pickInt("sinceBeforeDays"))
  }

  private def buildResponseBody(evaluationUsers: Seq[EvaluationUser], replyTo: Long)(implicit destination: BacklogApiClient.Destination): JsObject =
    Json.obj(
      "message" -> buildMessage(evaluationUsers),
      "replyTo" -> replyTo,
    )

  private def buildMessage(evaluationUsers: Seq[EvaluationUser])(implicit destination: BacklogApiClient.Destination): String =
    (evaluationUsers zip Stream.from(1)).map(tupled((evaluationUser, order) =>
      s"$order. [${evaluationUser.name}](https://${destination.domain}/user/${evaluationUser.userId}) ( ${evaluationUser.point} points )"
    ))
      .+:(typetalkMessageLabel)
      .mkString("\n")

}


