package models

import org.joda.time.DateTime
import org.virtuslab.unicorn.UnicornPlay._
import org.virtuslab.unicorn.UnicornPlay.driver.simple._
import play.api.libs.json.Json

case class SessionSongId(id: Long) extends AnyVal with BaseId
object SessionSongId extends IdCompanion[SessionSongId]

case class SessionSong(id: Option[SessionSongId] = None, singerId: SingerId, sessionId: SessionId, submitDate: DateTime = DateTime.now, title: String, artist: String) extends WithId[SessionSongId]

class SessionSongs(tag: Tag)
  extends IdTable[SessionSongId, SessionSong](tag, "session_songs") {

  def singerId = column[SingerId]("singer_id")
  def sessionId = column[SessionId]("session_id")
  def submitDate = column[DateTime]("submit_date")
  def title = column[String]("title")
  def artist = column[String]("artist")

  def * = (id.?, singerId, sessionId, submitDate, title, artist) <> (SessionSong.tupled, SessionSong.unapply)
}

object SessionSongFormatter {

  import SessionFormatter._
  import SingerFormatter._

  implicit val sessionSongIdFormat = Json.format[SessionSongId]
  implicit val sessionSongFormat = Json.format[SessionSong]
}

