package repositories

import models._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import play.api.libs.json.Json
import repositories.SessionSongRepositoryMessages.{SessionNotAcceptingSongs, InvalidSession, RequestSongErrors, RequestSongRequest}
import play.api.db.slick.{Session => DBSession}
import scalaz.Validation.FlatMap._

import scalaz.{Failure, Success, Validation}

/**
 * Created by hugh on 9/2/14.
 */

object SessionSongRepositoryMessages {

  case class RequestSongRequest(title: String, artist: String)

  sealed trait RequestSongErrors
  case class InvalidSession(sessionId: SessionId) extends RequestSongErrors
  case class SessionNotAcceptingSongs(sessionId: SessionId) extends RequestSongErrors


  object SessionSongComponentFormatter {
    import SessionFormatter._
    implicit val requestSongRequestFormat = Json.format[RequestSongRequest]
  }
}

trait SessionSongRepositoryComponent {
  this: SessionRepositoryComponent =>
  val sessionSongRepository = new SessionSongRepository

  class SessionSongRepository extends BaseIdRepository[SessionSongId, models.SessionSong, SessionSongs](TableQuery[SessionSongs]) {
    private val activeStates: Set[SongStatus] = Set(AwaitingApproval, Queued, OnDeck)

    def bySessionQuery(sessionId: SessionId) = query.filter(_.sessionId === sessionId)

    def requestSong(req: RequestSongRequest, singer: Singer)(implicit dbSession: DBSession): Validation[RequestSongErrors, SessionSong] = {
      val sessionSuccess = sessionRepository.findById(singer.sessionId) match {
        case None => Failure(InvalidSession(singer.sessionId))
        case Some(s) if !sessionRepository.isAcceptingRequests(s) => Failure(SessionNotAcceptingSongs(singer.sessionId))
        case Some(s) => Success(s)
      }

      sessionSuccess.flatMap { session =>
        val status = if(session.autoApprove) Queued else AwaitingApproval
        val unsaved = SessionSong(singerId = singer.id.get, sessionId = singer.sessionId, artist = req.artist, title = req.title, status = status)
        Success(unsaved.copy(id = Option(save(unsaved))))
      }
    }

    // For some reason _.status === Queued does not lift properly, so leave as a set operation
    def availableSongsByDateQuery(sessionId: SessionId) = query.filter(_.status inSet Set(Queued)).sortBy(_.submitDate)

    def activeSongsBySinger(singerId: SingerId)(implicit dbSession: DBSession) = {
      query.filter(_.singerId === singerId).filter(_.status inSet activeStates).list
    }
  }
}
