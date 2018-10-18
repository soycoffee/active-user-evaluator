package models

import java.time.LocalDateTime

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsObject, Reads}

case class Activity(`type`: Activity.Type, created: LocalDateTime, content: JsObject)

object Activity {

  abstract class Type(val id: Int, val group: TypeGroup)

  object Type {

    case object CreateIssue extends Type(1, TypeGroup.Management)
    case object UpdateIssue extends Type(2, TypeGroup.Management)
    case object CreateIssueComment extends Type(3, TypeGroup.Management)
    case object CreateWiki extends Type(5, TypeGroup.Document)
    case object UpdateWiki extends Type(6, TypeGroup.Document)
    case object CreateFile extends Type(8, TypeGroup.Document)
    case object UpdateFile extends Type(9, TypeGroup.Document)
    case object CreateGitPush extends Type(12, TypeGroup.Implement)
    case object CreateGitRepository extends Type(13, TypeGroup.Implement)
    case object UpdateMultiIssue extends Type(14, TypeGroup.Management)
    case object CreatePullRequest extends Type(18, TypeGroup.Implement)
    case object UpdatePullRequest extends Type(19, TypeGroup.Implement)
    case object CreatePullRequestComment extends Type(20, TypeGroup.Implement)
    case object CreateVersion extends Type(22, TypeGroup.Management)
    case object UpdateVersion extends Type(23, TypeGroup.Management)

    val Values = Seq(
      CreateIssue,
      UpdateIssue,
      CreateIssueComment,
      CreateWiki,
      UpdateWiki,
      CreateFile,
      UpdateFile,
      CreateGitRepository,
      UpdateMultiIssue,
      CreatePullRequest,
      UpdatePullRequest,
      CreatePullRequestComment,
      CreateVersion,
      UpdateVersion,
    )

  }

  abstract class TypeGroup

  object TypeGroup {

    case object Management extends TypeGroup
    case object Document extends TypeGroup
    case object Implement extends TypeGroup

  }

  implicit val readsFromTypeId: Reads[Activity] = (
    (__ \ "type").read[Int].map(id => Type.Values.find(_.id == id).get) and
    (__ \ "created").read(Reads.DefaultLocalDateTimeReads) and
    (__ \ "content").read[JsObject]
  ) (Activity.apply _)

  implicit val writes: OWrites[Activity] = (
    (__ \ "type").write[String].contramap[Type](_.toString) and
    (__ \ "created").write(Writes.DefaultLocalDateTimeWrites) and
    (__ \ "content").write[JsObject]
  ) (unlift(Activity.unapply))

}
