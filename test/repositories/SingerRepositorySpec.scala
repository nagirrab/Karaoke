package repositories

import fixtures.DBSpecBase
import models.SessionId

import scalaz.Failure


/**
 * Created by hugh on 9/6/14.
 */
class SingerRepositorySpec extends DBSpecBase {
  trait SingerSpecHelpers extends SingerRepositoryComponent with SessionRepositoryComponent with SessionSongRepositoryComponent

  "SingerRepository#joinSession" when {
    "given a valid session id" when {


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
