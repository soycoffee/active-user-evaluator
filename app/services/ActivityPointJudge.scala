package services

import models.Activity

class ActivityPointJudge {

  def apply(activity: Activity): Int =
    activity.`type` match {
      case Activity.Type.CreateGitPush => 0
      case _ => 1
    }

}
