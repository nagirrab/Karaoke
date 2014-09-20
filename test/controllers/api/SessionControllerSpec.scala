package controllers.api

import fixtures.SpecBase
import models.{UserId, SessionId, Session}
import org.mockito.Matchers
import play.api.db.slick.{Session => DBSession}
import play.api.libs.json.{JsValue, Json}
import play.api.test.{WithApplication, FakeRequest}

import repositories.{SessionSongRepositoryComponent, SessionRepositoryComponent}
import org.mockito.Mockito._
import play.api.test.Helpers._

import models.SessionFormatter._

/**
 * Created by hugh on 9/2/14.
 */
class SessionControllerSpec extends SpecBase {
  class TestController() extends SessionController with SessionRepositoryComponent with SessionSongRepositoryComponent {
    override val sessionRepository = mock[SessionRepository]
  }

  "SessionController#list" when {
    trait WithController {
      val controller = new TestController

      def getResult = controller.list().apply(FakeRequest())
    }

    "there are no sessions" should {
      trait NoSessions extends WithController {
        when(controller.sessionRepository.findAll()(Matchers.any[DBSession])).thenReturn(List.empty)
      }

      "return an empty array" in new NoSessions {
        contentAsJson(getResult) shouldEqual Json.toJson(List[Session]())
      }

      "have status 200" in new NoSessions {
        status(getResult) shouldEqual OK
      }
    }

    "there are sessions" should {
      trait WithSessions extends WithController {
        val sessions = List(Session(Some(SessionId(1)), "name", UserId(1), Some("password")), Session(Some(SessionId(2)), "another name", UserId(1), Some("another password")))
        when(controller.sessionRepository.findAll()(Matchers.any[DBSession])).thenReturn(sessions)
      }

      "return all sessions" in new WithSessions {
        contentAsJson(getResult) shouldEqual Json.toJson(sessions)
      }

      "have status 200" in new WithSessions {
        status(getResult) shouldEqual OK
      }
    }
  }

  "SessionController#create" when {
    trait WithController {
      val controller = new TestController
      def getResult(body: JsValue) = controller.create().apply(FakeRequest().withBody(body))
    }

    "given valid json" should {

      trait ValidJson extends WithController {
        val unsavedModel = models.Session(None, "test", UserId(1))
        val savedModel = models.Session(Some(SessionId(1)), "test", UserId(1))
        val data = Json.toJson(unsavedModel)

        when(controller.sessionRepository.save(Matchers.eq(unsavedModel))(Matchers.any[DBSession])).thenReturn(savedModel.id.value)
        when(controller.sessionRepository.findById(Matchers.eq(savedModel.id.value))(Matchers.any[DBSession])).thenReturn(Some(savedModel))
      }

      "create a new session" in new ValidJson {
        contentAsJson(getResult(data)) shouldEqual Json.toJson(savedModel)
      }

      "have status 201" in new ValidJson {
        status(getResult(data)) shouldEqual CREATED
      }
    }
    "given invalid data" should {
      val invalidData = Json.toJson(Map("invalid" -> "here"))

      "have status 400" in new WithController {
        status(getResult(invalidData)) shouldEqual BAD_REQUEST
      }
    }

  }

  "sessionController#show" when {
    trait WithController {
      val controller = new TestController
      def getResult(id: SessionId) = controller.show(id).apply(FakeRequest())
    }

    "given a valid id" should {
      trait ValidId extends WithController {
        val id = SessionId(1)
        val savedModel = models.Session(Some(id), "test", UserId(1))

        when(controller.sessionRepository.findById(Matchers.eq(id))(Matchers.any[DBSession])).thenReturn(Some(savedModel))
      }

      "return as json" in new ValidId {
        contentAsJson(getResult(id)) shouldEqual Json.toJson(savedModel)
      }

      "have status 200" in new ValidId {
        status(getResult(id)) shouldEqual OK
      }
    }
    "given an invalid id" should {
      trait BadId extends WithController {
        val id = SessionId(1)

        when(controller.sessionRepository.findById(Matchers.eq(id))(Matchers.any[DBSession])).thenReturn(None)
      }

      "return a 404" in new BadId {
        status(getResult(id)) shouldEqual NOT_FOUND
      }
    }
  }

  "SessionController#update" when {
    trait WithController {
      val controller = new TestController
      def getResult(id: SessionId, body: JsValue) = controller.update(id).apply(FakeRequest().withBody(body))
    }

    "given an existing id" when {
      "given valid json" should {

        trait ValidJson extends WithController {
          val previous = models.Session(Some(SessionId(1)), "test", UserId(1))
          val updated = models.Session(Some(SessionId(1)), "updated", UserId(1))
          val previousData = Json.toJson(previous)
          val updatedData = Json.toJson(updated)


          when(controller.sessionRepository.save(Matchers.eq(updated))(Matchers.any[DBSession])).thenReturn(updated.id.value)
          // Note that the different returns for subsequent calls
          when(controller.sessionRepository.findById(Matchers.eq(updated.id.value))(Matchers.any[DBSession])).thenReturn(Some(previous)).thenReturn(Some(updated))
        }

        "updates the session" in new ValidJson {
          contentAsJson(getResult(updated.id.get, updatedData)) shouldEqual Json.toJson(updatedData)
        }

        "have status 200" in new ValidJson {
          status(getResult(updated.id.get, updatedData)) shouldEqual OK
        }
      }
      "given invalid data" should {
        trait InvalidJson extends WithController {
          val invalidData = Json.toJson(Map("invalid" -> "here"))

          val previous = models.Session(Some(SessionId(1)), "test", UserId(1))

          when(controller.sessionRepository.findById(Matchers.eq(previous.id.value))(Matchers.any[DBSession])).thenReturn(Some(previous))
        }


        "have status 422" in new InvalidJson {
          status(getResult(SessionId(1), invalidData)) shouldEqual UNPROCESSABLE_ENTITY
        }
      }


    }



  }

}
