package repositories

import models._
import org.virtuslab.unicorn.UnicornPlay._
import org.virtuslab.unicorn.UnicornPlay.driver.simple._
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
    def bySessionQuery(sessionId: SessionId) = query.filter(_.sessionId === sessionId)

    def requestSong(req: RequestSongRequest, singer: Singer)(implicit dbSession: DBSession): Validation[RequestSongErrors, SessionSong] = {

      val sessionSuccess = sessionRepository.findById(singer.sessionId) match {
        case None => Failure(InvalidSession(singer.sessionId))
        case Some(s) if !sessionRepository.isAcceptingRequests(s) => Failure(SessionNotAcceptingSongs(singer.sessionId))
        case Some(s) => Success(s)
      }

      sessionSuccess.flatMap { session =>
        val unsaved = SessionSong(singerId = singer.id.get, sessionId = singer.sessionId, artist = req.artist, title = req.title)
        Success(unsaved.copy(id = Option(save(unsaved))))
      }
    }
  }
}
