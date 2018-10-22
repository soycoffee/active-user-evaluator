package controllers.evaluation

import javax.inject._
import models.Activity
import play.api.mvc._
import services.{EvaluationAggregator, UseApiDestination}

import scala.concurrent.ExecutionContext

@Singleton
class ManagementController @Inject()(
                                      val useApiDestination: UseApiDestination,
                                      val evaluationAggregator: EvaluationAggregator,
                                    )(implicit val ec: ExecutionContext) extends InjectedController
  with UserController
  with TypetalkController
  with HasTargetActivityTypeGroup
{

  override val targetActivityTypeGroup: Activity.TypeGroup = Activity.TypeGroup.Management
  override val typetalkMessageLabel = "マネジメント系アクティビティ ( 課題 / マイルストーン )"

}
