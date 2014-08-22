package repositories

import fixtures.DBSpecBase
import models.Session
import models._
import org.mindrot.jbcrypt.BCrypt
import org.virtuslab.unicorn.UnicornPlay.driver.simple._

import scala.slick.jdbc.JdbcBackend


/**
 * Created by hugh on 9/6/14.
 */
class SessionRepositorySpec extends DBSpecBase {
  trait SessionSpecHelpers extends SessionRepositoryComponent with SessionSongRepositoryComponent {

  }
  "SessionRepository#withSongs" when {

    "there are songs" should {
      "return the songs" in new SessionSpecHelpers {
        withRollback { implicit s =>
          val sessionId = sessionRepository.save(Session(name = "TestSession"))
          val session = sessionRepository.findById(sessionId).get

          val songId = sessionSongRepository.save(SessionSong(sessionId = sessionId, title = "Title", artist = "Artist"))
          val song = sessionSongRepository.findById(songId).get

          val song2Id = sessionSongRepository.save(SessionSong(sessionId = sessionId, title = "Title2", artist = "Artist2"))
          val song2 = sessionSongRepository.findById(song2Id).get

          val q = sessionRepository.withSongs(sessionId)

          q shouldBe Some((session, List(song, song2)))
        }
      }
    }
  }

}
