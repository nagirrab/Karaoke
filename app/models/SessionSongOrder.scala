package models

import org.virtuslab.unicorn.UnicornPlay.driver.simple._

case class SessionSongOrder(sessionId: SessionId, songId: SessionSongId, order: Int)

class SessionSongOrders(tag: Tag) extends Table[SessionSongOrder](tag, "session_song_orders") {
  def sessionId = column[SessionId]("session_id")
  def songId = column[SessionSongId]("song_id")
  def order = column[Int]("order")

  def * = (sessionId, songId, order) <> (SessionSongOrder.tupled, SessionSongOrder.unapply)
}

