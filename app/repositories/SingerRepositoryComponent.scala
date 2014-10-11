package repositories

import models._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import play.api.db.slick.{Session => DBSession}
import play.api.libs.json.Json
import repositories.SingerRepositoryMessages._

import scalaz.{Success, Failure, Validation}

/**
 * Created by hugh on 9/2/14.
 */

object SingerRepositoryMessages {
  case class JoinSessionRequest(sessionId: SessionId, name: String, password: Option[String] = None)

  sealed trait SessionErrors
  case class NameAlreadyTaken(name: String) extends SessionErrors
  case class NoSuchSession(sessionId: SessionId) extends SessionErrors
  case class InvalidPassword(sessionId: SessionId) extends SessionErrors

  object SingerRepositoryMessageFormatter {
    import SessionFormatter._
    implicit val joinSessionRequestFormatter = Json.format[JoinSessionRequest]
  }

}

trait SingerRepositoryComponent {
  this: SessionRepositoryComponent =>

  val singerRepository = new SingerRepository

  // This should really be a trait but unicorn doesn't support it right now.
  // We can still do a mock of the final class instead though.
  class SingerRepository extends BaseIdRepository[SingerId, models.Singer, Singers](TableQuery[Singers]) {

    def joinSession(req: JoinSessionRequest, user: Option[User] = None)(implicit dbSession: DBSession): Validation[SessionErrors, Singer] = {
      sessionRepository.findById(req.sessionId) match {
        case Some(session) => {
          if(session.password != req.password) {
            Failure(InvalidPassword(req.sessionId))
          } else if(query.filter(s => s.name === req.name && s.sessionId === req.sessionId).exists.run) {
            Failure(NameAlreadyTaken(req.name))
          } else {
            val newSinger = Singer(name = req.name, sessionId = req.sessionId, userId = user.flatMap(_.id))
            val singerId = save(newSinger)
            Success(newSinger.copy(id = Some(singerId)))
          }
        }
        case None => Failure(NoSuchSession(req.sessionId))
      }
    }
  }
}
