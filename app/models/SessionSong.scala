package models

import org.joda.time.DateTime
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import play.api.libs.json.Json

case class SessionSongId(id: Long) extends BaseId
object SessionSongId extends IdCompanion[SessionSongId]

case class SessionSong(id: Option[SessionSongId] = None, singerId: SingerId, sessionId: SessionId, songId: Option[SongId] = None,
                       submitDate: DateTime = DateTime.now, title: String, artist: String, externalLink: Option[String] = None, specialRequest: Option[String] = None,
                       status: SongStatus = AwaitingApproval, priority: Int = 0, notes: String = "") extends WithId[SessionSongId]

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
  def songId = column[Option[SongId]]("song_id")
  def submitDate = column[DateTime]("submit_date")
  def title = column[String]("title")
  def artist = column[String]("artist")
  def externalLink = column[Option[String]]("external_link")
  def specialRequest = column[Option[String]]("special_request")
  def status = column[SongStatus]("status")
  def priority = column[Int]("priority")
  def notes = column[String]("notes")

  def * = (id.?, singerId, sessionId, songId, submitDate, title, artist, externalLink, specialRequest, status, priority, notes) <> (SessionSong.tupled, SessionSong.unapply)
}

object SessionSongFormatter {
  import SessionFormatter._
  import SingerFormatter._
  import SongFormatter._

  implicit val sessionSongFormat = Json.format[SessionSong]
}

