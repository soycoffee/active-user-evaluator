package services

import javax.inject.Singleton
import models.Activity

@Singleton
class ActivityPointJudge {

  def apply(activity: Activity): Int =
    activity.`type` match {
      case Activity.Type.CreateGitPush => 0
      case _ => 1
    }

}
