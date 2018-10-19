package services

import javax.inject.Singleton
import models.Activity

@Singleton
class ActivityPointJudge {

  def apply(activity: Activity): Int =
    activity.`type` match {
      case Activity.Type.CreateGitPush =>
        activity.content.\("revision_count").validate[Int].getOrElse(1)
      case _ => 1
    }

}
