package controllers.evaluation

import models._

trait HasTargetActivityTypeGroup extends HasTargetActivityTypes {

  def targetActivityTypeGroup: Activity.TypeGroup

  lazy val targetActivityTypes: Seq[Activity.Type] =
    Activity.Type.Values.filter(_.group == targetActivityTypeGroup)

}


