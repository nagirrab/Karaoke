package services

import models._
import play.api.db.slick.Session
import play.api.db.slick.{Session => DBSession}
import play.api.libs.json.Json
import repositories.SessionSongRepositoryMessages.{RequestSongRequest, RequestSongErrors, GuestRequestSongRequest}
import repositories.{SingerRepositoryComponent, SessionSongRepositoryComponent, SessionRepositoryComponent}
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._

import scala.slick.lifted.TableQuery
import scalaz.{Failure, Success, Validation}

case class SessionDetails(session: models.Session, onDeck: Option[SessionSong], activeSongs: Seq[SessionSongOrder], songs: Seq[SessionSong], singers: Seq[Singer])
case class UpdateSongStatus(songId: SessionSongId, newStatus: SongStatus)

object SessionServiceFormatters {
  import SessionFormatter._
  import SessionSongFormatter._
  import SingerFormatter._
  import SessionSongOrderFormatter._
  implicit val detailsFormat = Json.format[SessionDetails]
  implicit val updatedSongStatusFormat = Json.format[UpdateSongStatus]
}

trait SessionServiceComponent {
  this: SessionRepositoryComponent with SessionSongRepositoryComponent with SingerRepositoryComponent =>

  val sessionService = new SessionService

  class SessionService {
    val orderingQuery = TableQuery[SessionSongOrders]

    def guestRequestSong(request: GuestRequestSongRequest)(implicit dbSession: DBSession): Validation[RequestSongErrors, SessionSong] = {
      sessionSongRepository.guestRequestSong(request) match {
        case s@Success(song) =>
          reorderSession(song.sessionId)
          s
        case f@Failure(error) => f
      }
    }

    def requestSong(request: RequestSongRequest, singer: Singer)(implicit dbSession: DBSession): Validation[RequestSongErrors, SessionSong] = {
      sessionSongRepository.requestSong(request, singer) match {
        case s@Success(song) =>
          reorderSession(song.sessionId)
          s
        case f@Failure(error) => f
      }
    }

    def advanceQueue(sessionId: SessionId)(implicit dbSession: DBSession): Option[SessionSong] = {
      val result = for (session <- sessionRepository.findById(sessionId)) yield {
        val songs  = sessionSongRepository.bySessionQuery(sessionId).list
        val currentSong = songs.find(_.status == OnDeck)
        val ordering  = orderingQuery.filter(_.sessionId === sessionId).sortBy(_.order).list

        dbSession.withTransaction {
          currentSong.map { cs =>
            val completedSong = cs.copy(status = Complete)
            sessionSongRepository.save(completedSong)
          }

          val nowOnDeck = for (nextSong <- chooseNextSong(ordering, songs)) yield {
            val updatedNextSong = nextSong.copy(status = OnDeck)
            sessionSongRepository.save(updatedNextSong)
            updatedNextSong
          }

          reorderSession(sessionId)
          nowOnDeck
        }
      }

      // I feel like there should be a nicer way to format all of this
      result.flatten
    }

    /**
     * Take the current song out, put the next one in place, then put the previous song at the top of the list
     * @param sessionId
     * @param dbSession
     * @return
     */
    def deferCurrentSong(sessionId: SessionId)(implicit dbSession: DBSession): Option[SessionSong] = {
      val result = for (session <- sessionRepository.findById(sessionId)) yield {
        val songs  = sessionSongRepository.bySessionQuery(sessionId).list
        val currentSong = songs.find(_.status == OnDeck)
        val ordering  = orderingQuery.filter(_.sessionId === sessionId).sortBy(_.order).list

        dbSession.withTransaction {
          val onHoldSong = currentSong.map { cs =>
            val completedSong = cs.copy(status = OnHold)
            sessionSongRepository.save(completedSong)
            completedSong
          }

          val nowOnDeck = for (nextSong <- chooseNextSong(ordering, songs)) yield {
            val updatedNextSong = nextSong.copy(status = OnDeck)
            sessionSongRepository.save(updatedNextSong)
            updatedNextSong
          }

          reorderSession(sessionId)

          nowOnDeck
        }
      }

      // I feel like there should be a nicer way to format all of this
      result.flatten
    }

    def playNow(sessionId: SessionId, songId: SessionSongId)(implicit dbSession: DBSession) = {
      for (session <- sessionRepository.findById(sessionId);
        song <- sessionSongRepository.findById(songId)) yield {
        dbSession.withTransaction {
          sessionSongRepository.onDeck(sessionId).map { onDeckBefore =>
            sessionSongRepository.save(onDeckBefore.copy(status = Queued))
          }

          val nowOnDeck = song.copy(status = OnDeck)
          sessionSongRepository.save(nowOnDeck)

          reorderSession(sessionId)
        }
      }
    }

    private def chooseNextSong(ordering: Seq[SessionSongOrder], songs: Seq[SessionSong]): Option[SessionSong] = {
      ordering.flatMap(o => songs.find(_.id == Some(o.songId))).find(_.status == Queued)
    }

    def updateSongStatus(message: UpdateSongStatus)(implicit dbSession: DBSession): Option[SessionSong] = {
      sessionSongRepository.findById(message.songId).map { song =>
        val updated = song.copy(status = message.newStatus)
        sessionSongRepository.save(updated)
        reorderSession(song.sessionId)
        updated
      }
    }

    def reorderSession(sessionId: SessionId)(implicit dbSession: DBSession): Seq[SessionSong] = {
      dbSession.withTransaction {
        val basicOrder: Seq[SessionSong] = sessionSongRepository.availableSongsByDateQuery(sessionId).list

        val newOrder = basicOrder.zipWithIndex.map { case (song, index) => SessionSongOrder(sessionId, song.id.get, index) }

        orderingQuery.filter(_.sessionId === sessionId).delete
        orderingQuery ++= newOrder

        basicOrder
      }
    }

    def details(sessionId: SessionId)(implicit dbSession: DBSession): Option[SessionDetails] = {
      for (session <- sessionRepository.findById(sessionId)) yield {
        val singers = singerRepository.findBySession(sessionId)
        val order = orderingQuery.filter(_.sessionId === sessionId).sortBy(_.order).list
        val songs = sessionSongRepository.bySessionQuery(sessionId).list
        val onDeck = songs.find(_.status == OnDeck)

        SessionDetails(session, onDeck = onDeck, activeSongs = order, singers = singers, songs = songs)
      }
    }
  }
}
