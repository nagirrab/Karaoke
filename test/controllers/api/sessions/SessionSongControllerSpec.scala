package controllers.api.sessions

import fixtures.{DBSpecBase, SingerSession, SpecBase}
import models._
import play.api.db.slick._
import play.api.libs.json.{JsSuccess, JsValue, Json}
import play.api.test.FakeRequest
import repositories.SessionSongRepositoryMessages.RequestSongRequest
import repositories.{SingerRepositoryComponent, SessionRepositoryComponent, SessionSongRepositoryComponent}
import org.mockito.Mockito._
import play.api.db.slick.{Session => DBSession}
import play.api.test.Helpers._

import repositories.SessionSongRepositoryMessages.SessionSongComponentFormatter._
import SessionSongFormatter._

import org.mockito.Matchers
import scalaz.Success

class SessionSongControllerSpec extends DBSpecBase with SingerSession {
  class TestController() extends SessionSongController with SessionSongRepositoryComponent
    with SessionRepositoryComponent with SingerRepositoryComponent {
    override val sessionSongRepository = mock[SessionSongRepository]
    override val singerRepository = mock[SingerRepository]
  }

  "SessionSongController#requestSong" when {
    abstract class WithController {
      implicit def dbSession: DBSession
      val controller = new TestController
      val req = RequestSongRequest("title", "artist")
      val reqJson = Json.toJson(req)
      val singerId = SingerId(1)
      val singer = Singer(Option(singerId), sessionId = SessionId(1), name = "Bob")
      when(controller.singerRepository.findById(Matchers.eq(singerId))(Matchers.any[DBSession])).thenReturn(Some(singer))

      def getResult(b: JsValue) = controller.requestSong().apply(FakeRequest().withBody(b).withSession(withSingerInSession(singer)))
    }

    "given valid data" should {
      abstract class SuccessfulRequest extends WithController {
        val song = SessionSong(id = Some(SessionSongId(1)), sessionId = singer.sessionId, artist = req.artist, title = req.title, singerId = singer.id.get)
        when(controller.sessionSongRepository.requestSong(Matchers.eq(req), Matchers.eq(singer))(Matchers.any[DBSession])).thenReturn(Success(song))
      }

      "return 200" in { implicit s =>
        new SuccessfulRequest {
          override implicit def dbSession = s
          status(getResult(reqJson)) shouldBe CREATED
        }
      }
      "return the new song" in { implicit s =>
        new SuccessfulRequest {
          override implicit def dbSession = s
          Json.fromJson[SessionSong](contentAsJson(getResult(reqJson))) match {
            case JsSuccess(`song`, _) => // pass
            case _ => fail
          }
        }
      }
    }
  }

}
