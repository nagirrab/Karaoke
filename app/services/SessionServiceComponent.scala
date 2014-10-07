package services

import models.{SessionSongOrder, SessionSong, SessionSongOrders, SessionId}
import play.api.db.slick.{Session => DBSession}
import repositories.{SessionSongRepositoryComponent, SessionRepositoryComponent}
import org.virtuslab.unicorn.UnicornPlay.driver.simple._

import scala.slick.lifted.TableQuery

trait SessionServiceComponent {
  this: SessionRepositoryComponent with SessionSongRepositoryComponent =>

  trait SessionService {
    val orderingQuery = TableQuery[SessionSongOrders]

    def reorderSession(sessionId: SessionId)(implicit dbSession: DBSession): Seq[SessionSong] = {
      dbSession.withTransaction {
        val basicOrder: Seq[SessionSong] = sessionSongRepository.availableSongsByDateQuery(sessionId).list

        val newOrder = basicOrder.zipWithIndex.map { case (song, index) => SessionSongOrder(sessionId, song.id.get, index) }

        orderingQuery.filter(_.sessionId === sessionId).delete
        orderingQuery ++= newOrder

        basicOrder
      }
    }
  }
}
