package models

import org.virtuslab.unicorn.UnicornPlay._
import org.virtuslab.unicorn.UnicornPlay.driver.simple._
import play.api.libs.json.Json

/**
 * Created by hugh on 8/27/14.
 */

case class UserId(id: Long) extends AnyVal with BaseId
object UserId extends IdCompanion[UserId]

case class User(id: Option[UserId], name: String, email: String, passwordHash: String, passwordSalt: String) extends WithId[UserId]

case class UserLoginAttempt(email: String, rawPassword: String)

case class UserCreationAttempt(name: String, email: String, password: String)

case class UserCreationFailed(reason: String)



class Users(tag: Tag)
  extends IdTable[UserId, User](tag, "users") {

  def name = column[String]("name")

  def email = column[String]("email")

  def passwordHash = column[String]("password_hash")

  def passwordSalt = column[String]("password_salt")

  def * = (id.?, name, email, passwordHash, passwordSalt) <> (User.tupled, User.unapply)
}

object UserFormatter {
  implicit val userIdReads = Json.reads[UserId]
  implicit val userReads = Json.reads[User]
  implicit val userIdWrites = Json.writes[UserId]
  implicit val userWrites = Json.writes[User]
  implicit val userLoginAttemptReads = Json.reads[UserLoginAttempt]
  implicit val userLoginAttemptWrites = Json.writes[UserLoginAttempt]
}