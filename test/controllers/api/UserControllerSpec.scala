package controllers.api

import fixtures.{DBSpecBase, SpecBase}
import models.UserFormatter._
import models.{UserLoginAttempt, User, UserId}
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.db.slick.{Session => DBSession}
import play.api.libs.json.{JsNull, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.UserRepositoryComponent

/**
 * Created by hugh on 9/2/14.
 */
class UserControllerSpec extends SpecBase {
  class TestController() extends UserController with UserRepositoryComponent {
    override val userRepository = mock[UserRepository]
  }

  "UserController#login" when {
    trait WithController {
      val controller = new TestController

      def getResult(b: JsValue) = controller.login().apply(FakeRequest().withBody(b))
    }

    "invalid data is given" should {
      "return a bad request" in new WithController {
        status(getResult(JsNull)) shouldEqual BAD_REQUEST
      }
    }

    "the user does not exist or the password was wrong" should {
      val badRequest = UserLoginAttempt("null@void.com", "badPassword")
      val badRequestJson = Json.toJson(badRequest)

      trait InvalidLoginAttempt extends WithController {
        when(controller.userRepository.login(Matchers.eq(badRequest))(Matchers.any[DBSession])).thenReturn(None)
      }

      "return a bad request" in new InvalidLoginAttempt {
        status(getResult(badRequestJson)) shouldBe BAD_REQUEST
      }

      "not set the session" in new InvalidLoginAttempt {
        session(getResult(badRequestJson)).get("userId") shouldBe None
      }
    }

    "the user exists and the password is valid" should {
      val goodRequest = UserLoginAttempt("null@void.com", "password")
      val goodRequestJson = Json.toJson(goodRequest)
      val user = User(id = Some(UserId(1)), name = "User", email =" null@void.com", passwordHash = "", passwordSalt = "")

      trait ValidLoginAttempt extends WithController {
        when(controller.userRepository.login(Matchers.eq(goodRequest))(Matchers.any[DBSession])).thenReturn(Some(user))
      }

      "return ok" in new ValidLoginAttempt {
        status(getResult(goodRequestJson)) shouldBe OK
      }

      "set the userid in the session" in new ValidLoginAttempt {
        session(getResult(goodRequestJson)).get("userId") shouldBe Some(user.id.get.id.toString)
      }
    }

  }

  "UserController#logout" when {
    trait WithController {
      val controller = new TestController
      def existingUser: Option[(String, String)] = None
      def getResult = controller.logout().apply(FakeRequest().withSession(existingUser.toList:_*))
    }

    "a user is logged in" should {
      trait LoggedInUser extends WithController {
        override def existingUser = Some(("userId", "1"))
      }
      "remove the user from the session" in new LoggedInUser {
        session(getResult).get("userId") shouldBe None
      }

      "return ok" in new LoggedInUser {
        status(getResult) shouldBe OK
      }
    }

  }

}
