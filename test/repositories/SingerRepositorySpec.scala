package repositories

import fixtures.DBSpecBase
import models.{Singer, UserId, Session, SessionId}

import scalaz.{Success, Failure}
import play.api.db.slick.{Session => DBSession}

/**
 * Created by hugh on 9/6/14.
 */
class SingerRepositorySpec extends DBSpecBase {
  trait SingerSpecHelpers extends SingerRepositoryComponent with SessionRepositoryComponent with SessionSongRepositoryComponent

  "SingerRepository#joinSession" when {
    "given a valid session id" when {
      class ExistingSession(implicit val dbSession: DBSession) extends SingerSpecHelpers {
        val sessionId = sessionRepository.save(Session(name = "test session", userId = UserId(1)))
        val session = sessionRepository.findById(sessionId).get
      }

      "there is no password" when {
        "the name is not taken" should {
          "create the singer successfully" in { s =>
            new ExistingSession()(s) { // The setup steps require a session, which we pass in like this to make the implicits work
              singerRepository.joinSession(JoinSessionRequest(sessionId, "Bob")) match {
                case Success(singer) =>
                  singer.id shouldNot be(None)
                  singer.name shouldBe "Bob"
                case _ => fail("no singer")
              }
            }
          }
        }

        "the name is taken" should {
          class ExistingSinger(override implicit val dbSession: DBSession) extends ExistingSession()(dbSession) {
            val existingSingerId = singerRepository.save(Singer(sessionId = sessionId, name = "Bob"))
          }

          "give an error" in { s =>
            new ExistingSinger()(s) {
              singerRepository.joinSession(JoinSessionRequest(sessionId, "Bob")) shouldBe Failure(NameAlreadyTaken("Bob"))
            }
          }

        }
      }

      "there is a password" when {
        "the password is invalid" should {

        }

        "the password is correct" should {

        }
      }

    }

    "given an invalid session id" should {
      "return a failure" in { implicit s =>
        new SingerSpecHelpers {
          singerRepository.joinSession(JoinSessionRequest(SessionId(123), "Bob")) shouldBe Failure(NoSuchSession(SessionId(123)))
        }
      }
    }

  }

}
