package models

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import play.api.libs.json.Json


case class SingerId(id: Long) extends BaseId
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

  implicit val singerFormat = Json.format[Singer]
}