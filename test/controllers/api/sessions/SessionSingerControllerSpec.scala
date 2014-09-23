package controllers.api.sessions

import fixtures.SpecBase
import org.mockito.Mockito._
import models._
import org.mockito.Matchers
import play.api.db.slick.{Session => DBSession}
import play.api.libs.json.{Json, JsNull, JsValue}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SingerRepositoryMessages.JoinSessionRequest
import repositories.{SessionSongRepositoryComponent, SessionRepositoryComponent, SingerRepositoryComponent, UserRepositoryComponent}
import SingerFormatter._

import scalaz.Success

import repositories.SingerRepositoryMessages.SingerComponentFormatter._

class SessionSingerControllerSpec extends SpecBase {
  class TestController() extends SessionSingerController with SingerRepositoryComponent
  with SessionRepositoryComponent
  with SessionSongRepositoryComponent {
    override val singerRepository = mock[SingerRepository]
  }

  "SessionSingerController#join" when {
    trait WithController {
      val controller = new TestController
      val req = JoinSessionRequest(SessionId(1), name = "Bob")
      val reqJson = Json.toJson(req)

      def getResult(b: JsValue) = controller.join().apply(FakeRequest().withBody(b))
    }

    "given a valid request" when {
      "it succeeds" should {
        trait SuccessfulRequest extends WithController {
          val response = Singer(Option(SingerId(1)), name = req.name, sessionId = req.sessionId)
          when(controller.singerRepository.joinSession(Matchers.eq(req), Matchers.eq(None))(Matchers.any[DBSession])).thenReturn(Success(response))
        }

        "return 200" in new SuccessfulRequest {
          status(getResult(reqJson)) shouldBe OK
        }

        "add the singer to the session" in new SuccessfulRequest {
          session(getResult(reqJson)).get("singerId") shouldBe Some(response.id.get.id.toString)
        }

        "return the new singer" in new SuccessfulRequest {
          contentAsJson(getResult(reqJson)) shouldBe Json.toJson(response)
        }
      }

      "it fails" should {

        "remove the singer from the session" in pending

        "return a bad request" in pending
      }
    }

    "given an invalid request" should {
      "return a bad request" in pending
    }

  }

}
