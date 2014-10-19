package models

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import play.api.libs.json.Json

/**
 * Created by hugh on 8/27/14.
 */

case class UserId(id: Long) extends BaseId
object UserId extends IdCompanion[UserId]

case class User(id: Option[UserId], name: String, email: String, passwordHash: String, passwordSalt: String) extends WithId[UserId]

class Users(tag: Tag)
  extends IdTable[UserId, User](tag, "users") {

  def name = column[String]("name")

  def email = column[String]("email")

  def passwordHash = column[String]("password_hash")

  def passwordSalt = column[String]("password_salt")

  def * = (id.?, name, email, passwordHash, passwordSalt) <> (User.tupled, User.unapply)
}

object UserFormatter {
  implicit val userFormat = Json.format[User]
}