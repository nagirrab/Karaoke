package repositories

import models._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.{Session => DBSession}

/**
 * Created by hugh on 9/2/14.
 */

trait UserRepositoryComponent {
  val userRepository = new UserRepository

  // This should really be a trait but unicorn doesn't support it right now.
  // We can still do a mock of the final class instead though.
  class UserRepository extends BaseIdRepository[UserId, models.User, Users](TableQuery[Users]) {

    def findByEmailQuery(email: String) = query.filter(_.email === email)

    def login(attempt: UserLoginAttempt)(implicit session: DBSession): Option[User] = {
      val userByEmail = findByEmailQuery(attempt.email).run.headOption

      userByEmail.filter { u =>
        val passwordHashAttempt = BCrypt.hashpw(attempt.email, u.passwordSalt)
        passwordHashAttempt == u.passwordHash
      }
    }

    def create(attempt: UserCreationAttempt)(implicit session: DBSession): Either[UserCreationFailed, User] = {

      val existingUser = findByEmailQuery(attempt.email).run.headOption
      existingUser match {
        case Some(_) => Left(UserCreationFailed("email taken"))
        case None => {
          val salt = BCrypt.gensalt()
          val passwordHash = BCrypt.hashpw(attempt.password, salt)
          val u = User(id = None, email = attempt.email, name = attempt.name, passwordHash = passwordHash, passwordSalt = salt)

          findById(save(u)) match {
            case Some(user) => Right(user)
            case None => Left(UserCreationFailed("unknown error"))
          }
        }
      }
    }
  }
}
