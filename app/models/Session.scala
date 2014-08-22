package models

import org.virtuslab.unicorn.UnicornPlay._
import org.virtuslab.unicorn.UnicornPlay.driver.simple._
import play.api.libs.json.Json

case class SessionId(id: Long) extends AnyVal with BaseId
object SessionId extends IdCompanion[SessionId]

case class Session(id: Option[SessionId] = None, name: String, password: Option[String] = None,
                   autoApprove: Boolean = true, autoOrder: Boolean = true, notes: String = "") extends WithId[SessionId]

class Sessions(tag: Tag)
  extends IdTable[SessionId, Session](tag, "sessions") {

  def name = column[String]("name")
  //def startDate = column[DateTime]("start_date")
  //def endDate = column[DateTime]("end_date")
  def password = column[Option[String]]("password", O.Nullable)
  def autoApprove = column[Boolean]("auto_approve")
  def autoOrder = column[Boolean]("auto_order")
  def notes = column[String]("notes")


  //def * = (id, name, startDate, endDate, password, autoApprove, autoOrder, notes) <> (Session.tupled, Session.unapply)
  def * = (id.?, name, password, autoApprove, autoOrder, notes) <> (Session.tupled, Session.unapply)
}

object SessionFormatter {
  implicit val sessionIdReads = Json.reads[SessionId]
  implicit val sessionReads = Json.reads[Session]
  implicit val sessionIdWrites = Json.writes[SessionId]
  implicit val sessionWrites = Json.writes[Session]
}