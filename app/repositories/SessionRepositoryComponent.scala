package repositories

import models._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import play.api.db.slick.{Session => DBSession}


/**
 * Created by hugh on 9/2/14.
 */

trait SessionRepositoryComponent {
  self: SessionSongRepositoryComponent =>
  val sessionRepository = new SessionRepository

  // This should really be a trait but unicorn doesn't support it right now.
  // We can still do a mock of the final class instead though.
  class SessionRepository extends BaseIdRepository[SessionId, models.Session, Sessions](TableQuery[Sessions]) {

    def withSongs(id: SessionId)(implicit dbSession: DBSession): Option[(models.Session, Seq[SessionSong])] = {
      findById(id).map(s => (s, sessionSongRepository.bySessionQuery(id).list))
    }

    def active(implicit dbSession: DBSession): Seq[models.Session] = query.filter(_.status inSet openStates).list

    private val acceptingSongsStates: Set[SessionStatus] = Set(AcceptingRequests, Open)
    private val openStates: Set[SessionStatus] = Set(AwaitingOpen, AcceptingRequests, Open, NoMoreRequests)

    def isAcceptingRequests(session: models.Session): Boolean = acceptingSongsStates.contains(session.status)
  }
}
