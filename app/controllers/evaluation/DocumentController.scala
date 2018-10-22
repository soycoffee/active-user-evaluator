package controllers.evaluation

import javax.inject._
import models.Activity
import play.api.mvc._
import services.{EvaluationAggregator, UseApiDestination}

import scala.concurrent.ExecutionContext

@Singleton
class DocumentController @Inject()(
                                    val useApiDestination: UseApiDestination,
                                    val evaluationAggregator: EvaluationAggregator,
                                  )(implicit val ec: ExecutionContext) extends InjectedController
  with JsonController
  with TypetalkController
  with HasTargetActivityTypeGroup
{

  override val targetActivityTypeGroup: Activity.TypeGroup = Activity.TypeGroup.Document
  override val typetalkMessageLabel: String = "ドキュメント系アクティビティ ( Wiki / ファイル )"

}
