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

case class SessionDetails(session: models.Session, activeSongs: Seq[SessionSongOrder], songs: Seq[SessionSong], singers: Seq[Singer])

object SessionServiceFormatters {
  import SessionFormatter._
  import SessionSongFormatter._
  import SingerFormatter._
  import SessionSongOrderFormatter._
  implicit val detailsFormat = Json.format[SessionDetails]
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

        SessionDetails(session, activeSongs = order, singers = singers, songs = songs)
      }
    }
  }
}
