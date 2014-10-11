package repositories

import fixtures.{DBSpecBase, SpecBase}
import models.{UserCreationFailed, UserCreationAttempt, User}
import org.mindrot.jbcrypt.BCrypt
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import scala.slick.jdbc.JdbcBackend
import play.api.db.slick.{DB, DBAction}


/**
 * Created by hugh on 9/6/14.
 */
class UserRepositorySpec extends DBSpecBase {
  trait UserSpecHelpers extends UserRepositoryComponent {
    def createUser(name: String = "Test", email: String = randomString(), password: String = "password")(implicit session: JdbcBackend#SessionDef) = {
      val passwordSalt = BCrypt.gensalt()
      val passwordHash = BCrypt.hashpw(password, passwordSalt)
      val unsavedUser = User(id = None, name = name, email = email, passwordHash = passwordHash, passwordSalt = passwordSalt)
      userRepository.findById(userRepository.save(unsavedUser)).get
    }
  }
  "UserRepository#findByEmailQuery" when {

    "the email exists" should {
      "return the user" in { implicit s =>
        new UserSpecHelpers {
          val email = randomString()
          val existingUser = createUser(email = email)
          val test = userRepository.findById(models.UserId(1))
          userRepository.findByEmailQuery(email).run.headOption shouldEqual Some(existingUser)
        }
      }
    }

    "the email does not exist" should {
      "return none" in { implicit s =>
        new UserSpecHelpers {
          userRepository.findByEmailQuery(randomString()).run.headOption shouldEqual None
        }
      }
    }
  }

  "UserRepository#create" when {
    "given a new email address" should {
      trait NewUser extends UserSpecHelpers {
        val name = "Bob"
        val password = "password"
        val attempt = UserCreationAttempt(name, randomString(), password)
      }

      "create the user" in { implicit s =>
        new NewUser {
          val result = userRepository.create(attempt)
          val createdUser = result.right.get
          createdUser.email shouldBe attempt.email
          createdUser.name shouldBe attempt.name
          createdUser.passwordHash shouldBe BCrypt.hashpw(password, createdUser.passwordSalt)
        }
      }
    }

    "given a taken email address" should {
      trait ExistingUser extends UserSpecHelpers {
        val usedEmail = randomString()
        val attempt = UserCreationAttempt("Bob", usedEmail, "password")
      }

      "return an error" in { implicit s =>
        new ExistingUser {
          createUser(email = usedEmail)
            userRepository.create(attempt) shouldBe Left(UserCreationFailed("email taken"))
        }
      }
    }
  }
}
