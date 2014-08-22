package models

import org.joda.time.DateTime
import org.virtuslab.unicorn.UnicornPlay._
import org.virtuslab.unicorn.UnicornPlay.driver.simple._
import play.api.libs.json.Json

case class SessionSongId(id: Long) extends AnyVal with BaseId
object SessionSongId extends IdCompanion[SessionSongId]

case class SessionSong(id: Option[SessionSongId] = None, sessionId: SessionId, submitDate: DateTime = DateTime.now, title: String, artist: String) extends WithId[SessionSongId]

class SessionSongs(tag: Tag)
  extends IdTable[SessionSongId, SessionSong](tag, "session_songs") {

  def sessionId = column[SessionId]("session_id")
  def submitDate = column[DateTime]("submit_date")
  def title = column[String]("title")
  def artist = column[String]("artist")

  def * = (id.?, sessionId, submitDate, title, artist) <> (SessionSong.tupled, SessionSong.unapply)
}

object SessionSongFormatter {
  import SessionFormatter._
  implicit val sessionSongIdReads = Json.reads[SessionSongId]
  implicit val sessionSongReads = Json.reads[SessionSong]
  implicit val sessionSongIdWrites = Json.writes[SessionSongId]
  implicit val sessionSongWrites = Json.writes[SessionSong]
}

