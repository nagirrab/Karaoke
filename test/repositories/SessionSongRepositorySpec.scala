package repositories

import fixtures.DBSpecBase
import models._
import repositories.SessionSongRepositoryMessages.{SessionNotAcceptingSongs, InvalidSession, RequestSongRequest}

import scalaz.{Success, Failure}

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
            case Failure(_) => fail
          }

        }
      }
    }
  }

}
