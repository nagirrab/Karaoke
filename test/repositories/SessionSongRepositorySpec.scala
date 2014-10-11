package repositories

import fixtures.DBSpecBase
import models.Session
import models._
import org.joda.time.DateTime
import play.api.db.slick._
import repositories.SessionSongRepositoryMessages.{SessionNotAcceptingSongs, InvalidSession, RequestSongRequest}
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import scalaz.{Success, Failure}
import play.api.db.slick.{Session => DBSession}

class SessionSongRepositorySpec extends DBSpecBase {
  abstract class SessionBase extends SessionSongRepositoryComponent with SessionRepositoryComponent

  "SessionSongRepository#requestSong" when {
    "the session does not exist" should {
      "return an error" in { implicit dbSession =>
        new SessionBase {
          val singer = Singer(id = Some(SingerId(1)), sessionId = SessionId(5555), name = "Bob")
          sessionSongRepository.requestSong(RequestSongRequest("title", "artist"), singer) shouldBe Failure(InvalidSession(SessionId(5555)))
        }
      }
    }
    "the session is not accepting requests" should {
      "return an error" in { implicit dbSession =>
        new SessionBase {
          val sessionId = sessionRepository.save(Session(status = AwaitingOpen, name = "TestSession", userId = UserId(1)))
          val session = sessionRepository.findById(sessionId).get

          val singer = Singer(id = Some(SingerId(1)), sessionId = sessionId, name = "Bob")

          sessionSongRepository.requestSong(RequestSongRequest("title", "artist"), singer) shouldBe Failure(SessionNotAcceptingSongs(sessionId))
        }
      }
    }
    "every is in order" should {
      "return the new saved song" in { implicit dbSession =>
        new SessionBase {
          val sessionId = sessionRepository.save(Session(status = Open, name = "TestSession", userId = UserId(1)))
          val session = sessionRepository.findById(sessionId).get

          val singer = Singer(id = Some(SingerId(1)), sessionId = sessionId, name = "Bob")

          sessionSongRepository.requestSong(RequestSongRequest("title", "artist"), singer) match {
            case Success(s) =>
              s.id shouldNot be(None)
              s.artist shouldBe "artist"
              s.title shouldBe "title"
              s.singerId shouldBe singer.id.get
              s.sessionId shouldBe sessionId
            case Failure(_) => fail()
          }
        }
      }
    }
  }

  "SessionSongRepository#availableSongsByDateQuery" when {
    abstract class EmptySession extends SessionBase {
      implicit def dbSession: DBSession
      val sessionId = sessionRepository.save(Session(status = Open, name = "TestSession", userId = UserId(1)))
      val session = sessionRepository.findById(sessionId).get
    }

    "there are no songs" should {
      "return an empty list" in { implicit s =>
        new EmptySession {
          def dbSession = s

          sessionSongRepository.availableSongsByDateQuery(sessionId).list shouldBe Nil
        }
      }
    }

    "there are songs" should {
      abstract class FullSession extends EmptySession {
        val songs = (1 to 5).zip(List(Queued, Queued, Complete, Queued, Queued)).map({ case (index, status) =>
          sessionSongRepository.findById(sessionSongRepository.save(SessionSong(sessionId = sessionId, singerId = SingerId(1),
            artist = s"artist$index", title = s"title$index", submitDate = DateTime.now.minus(index))))
        }).flatten
      }
      "return an empty list" in { implicit s =>
        new FullSession {
          def dbSession = s

          val expectedOrder = songs.filter(_.status == Queued).sortBy(_.submitDate.getMillis)

          sessionSongRepository.availableSongsByDateQuery(sessionId).list shouldBe expectedOrder
        }
      }
    }
  }

}
