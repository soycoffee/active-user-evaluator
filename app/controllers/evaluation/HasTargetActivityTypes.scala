package controllers.evaluation

import models._

trait HasTargetActivityTypes {

  def targetActivityTypes: Seq[Activity.Type]

}


