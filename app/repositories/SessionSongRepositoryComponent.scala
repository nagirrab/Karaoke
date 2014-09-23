package repositories

import models._
import org.virtuslab.unicorn.UnicornPlay._
import org.virtuslab.unicorn.UnicornPlay.driver.simple._

/**
 * Created by hugh on 9/2/14.
 */

trait SessionSongRepositoryComponent {
  val sessionSongRepository = new SessionSongRepository

  class SessionSongRepository extends BaseIdRepository[SessionSongId, models.SessionSong, SessionSongs](TableQuery[SessionSongs]) {
    def bySessionQuery(sessionId: SessionId) = query.filter(_.sessionId === sessionId)
  }
}
