package models

import org.joda.time.DateTime
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import play.api.libs.json.{DefaultReads, Json}

case class SessionId(id: Long) extends BaseId
object SessionId extends IdCompanion[SessionId]

sealed trait SessionStatus

case object AwaitingOpen extends SessionStatus
case object AcceptingRequests extends SessionStatus
case object Open extends SessionStatus
case object NoMoreRequests extends SessionStatus
case object Closed extends SessionStatus

object SessionStatus extends SerializableEnum[SessionStatus] {
  import scala.reflect._
  val ct = classTag[SessionStatus]
  def mapping = Map[String, SessionStatus](
    "AWAITING_OPEN" -> AwaitingOpen,
    "ACCEPTING_REQUESTS" -> AcceptingRequests,
    "OPEN" -> Open,
    "NO_MORE_REQUESTS" -> NoMoreRequests,
    "CLOSED" -> Closed
  )
}

case class Session(id: Option[SessionId] = None, name: String, userId: UserId,
                   password: Option[String] = None, startDate: DateTime = DateTime.now,
                   endDate: Option[DateTime] = None,
                   autoApprove: Boolean = true, autoOrder: Boolean = true,
                   status: SessionStatus = Open, notes: String = "") extends WithId[SessionId]

class Sessions(tag: Tag)
  extends IdTable[SessionId, Session](tag, "sessions") {

  def name = column[String]("name")
  def userId = column[UserId]("user_id")
  def startDate = column[DateTime]("start_date")
  def endDate = column[Option[DateTime]]("end_date")
  def password = column[Option[String]]("password", O.Nullable)
  def autoApprove = column[Boolean]("auto_approve")
  def autoOrder = column[Boolean]("auto_order")
  def status = column[SessionStatus]("status")
  def notes = column[String]("notes")

  def * = (id.?, name, userId, password, startDate, endDate, autoApprove, autoOrder, status, notes) <> (Session.tupled, Session.unapply)
}

object SessionFormatter extends DefaultReads {
  implicit val dateTimeReads = jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  implicit val sessionFormat = Json.format[Session]
}