package models

import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import play.api.libs.json.Json

case class SessionSongOrder(sessionId: SessionId, songId: SessionSongId, order: Int)

class SessionSongOrders(tag: Tag) extends Table[SessionSongOrder](tag, "session_song_orders") {
  def sessionId = column[SessionId]("session_id")
  def songId = column[SessionSongId]("song_id")
  def order = column[Int]("order")

  def * = (sessionId, songId, order) <> (SessionSongOrder.tupled, SessionSongOrder.unapply)
}

object SessionSongOrderFormatter {
  implicit val orderFormatter = Json.format[SessionSongOrder]
}
