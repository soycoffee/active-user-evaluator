package services

import models.Activity

class ActivityPointAggregator {

  def apply(activity: Activity): Int =
    activity.`type` match {
      case Activity.Type.CreateGitPush => 0
      case _ => 1
    }

}
