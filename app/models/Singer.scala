package models

import org.virtuslab.unicorn.UnicornPlay._
import org.virtuslab.unicorn.UnicornPlay.driver.simple._
import play.api.libs.json.Json


case class SingerId(id: Long) extends AnyVal with BaseId
object SingerId extends IdCompanion[SingerId]

case class Singer(id: Option[SingerId] = None, name: String, sessionId: SessionId, userId: Option[UserId] = None) extends WithId[SingerId]


class Singers(tag: Tag)
  extends IdTable[SingerId, Singer](tag, "singers") {

  def name = column[String]("name")
  def sessionId = column[SessionId]("session_id")
  def userId = column[Option[UserId]]("user_id")

  def * = (id.?, name, sessionId, userId) <> (Singer.tupled, Singer.unapply)
}

object SingerFormatter {
  import UserFormatter._ //needed by the singer serializer
  import SessionFormatter._
  implicit val singerIdReads = Json.reads[SingerId]
  implicit val singerReads = Json.reads[Singer]
  implicit val singerIdWrites = Json.writes[SingerId]
  implicit val singerWrites = Json.writes[Singer]
}