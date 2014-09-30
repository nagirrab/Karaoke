package models

import org.joda.time.DateTime
import org.virtuslab.unicorn.UnicornPlay._
import org.virtuslab.unicorn.UnicornPlay.driver.simple._
import play.api.libs.json.Json

case class SessionSongId(id: Long) extends AnyVal with BaseId
object SessionSongId extends IdCompanion[SessionSongId]

case class SessionSong(id: Option[SessionSongId] = None, singerId: SingerId, sessionId: SessionId,
                       submitDate: DateTime = DateTime.now, title: String, artist: String,
                       status: SongStatus = AwaitingApproval, priority: Int = 0) extends WithId[SessionSongId]

sealed trait SongStatus

case object AwaitingApproval extends SongStatus
case object Queued extends SongStatus
case object OnDeck extends SongStatus
case object OnHold extends SongStatus
case object Complete extends SongStatus
case object Cancelled extends SongStatus
case object Duplicate extends SongStatus

object SongStatus extends SerializableEnum[SongStatus] {
  import scala.reflect._
  val ct = classTag[SongStatus]
  def mapping = Map[String, SongStatus](
    "AWAITING_APPROVAL" -> AwaitingApproval,
    "QUEUED" -> Queued,
    "ON_DECK" -> OnDeck,
    "ON_HOLD" -> OnHold,
    "COMPLETE" -> Complete,
    "CANCELLED" -> Cancelled,
    "DUPLICATE" -> Duplicate
  )
}

class SessionSongs(tag: Tag)
  extends IdTable[SessionSongId, SessionSong](tag, "session_songs") {

  def singerId = column[SingerId]("singer_id")
  def sessionId = column[SessionId]("session_id")
  def submitDate = column[DateTime]("submit_date")
  def title = column[String]("title")
  def artist = column[String]("artist")
  def status = column[SongStatus]("status")
  def priority = column[Int]("priority")

  def * = (id.?, singerId, sessionId, submitDate, title, artist, status, priority) <> (SessionSong.tupled, SessionSong.unapply)
}

object SessionSongFormatter {

  import SessionFormatter._
  import SingerFormatter._

  implicit val sessionSongIdFormat = Json.format[SessionSongId]
  implicit val sessionSongFormat = Json.format[SessionSong]
}

