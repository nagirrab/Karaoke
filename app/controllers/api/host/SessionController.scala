package controllers.api.host

import controllers.actions.Security
import models.SessionFormatter._
import models.{SessionSongId, SessionSongFormatter, SessionId}
import play.api.Play.current
import play.api.db.slick.{DB, DBAction}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import repositories.{SingerRepositoryComponent, SessionSongRepositoryComponent, SessionRepositoryComponent}
import services.{UpdateSongStatus, SessionServiceComponent}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object SessionController extends SessionController with SessionRepositoryComponent with SessionSongRepositoryComponent with SingerRepositoryComponent with SessionServiceComponent

trait SessionController extends Controller with Security {
  self: SessionRepositoryComponent with SessionServiceComponent =>
  def create() = DBAction(parse.json) { rs =>
    implicit val session = rs.dbSession

    val newSession = Json.fromJson[models.Session](rs.body)

    newSession.map(sessionRepository.save).map(sessionRepository.findById) match {
      case JsSuccess(r, _) => Created(Json.toJson(r))
      case JsError(errors) => BadRequest(errors.toString())
    }
  }

  def list() = Action.async { req =>
    Future {
      DB.withSession { implicit s =>
        Ok(Json.toJson(sessionRepository.findAll()))
      }
    }
  }

  def active() = Action.async { req =>
    Future {
      DB.withSession { implicit s =>
        Ok(Json.toJson(sessionRepository.active))
      }
    }
  }

  def show(id: SessionId) = Action.async { req =>
    Future {
      DB.withSession { implicit s =>
        sessionRepository.findById(id) match {
          case Some(session) => Ok(Json.toJson(session))
          case _ => NotFound(Json.toJson(Map("error" -> "Not Found")))
        }
      }
    }
  }

  def details(id: SessionId) = Action.async { req =>
    import services.SessionServiceFormatters._
    Future {
      DB.withSession { implicit s =>
        sessionService.details(id) match {
          case Some(details) => Ok(Json.toJson(details))
          case _ => NotFound(Json.toJson(Map("error" -> "Not Found")))
        }
      }
    }
  }

  def advance(id: SessionId) = Action.async { req =>
    import SessionSongFormatter._
    import services.SessionServiceFormatters._
    Future {
      DB.withSession { implicit s =>
        sessionService.advanceQueue(id)
        Ok
      }
    }
  }

  def deferCurrentSong(id: SessionId) = Action.async { req =>
    import SessionSongFormatter._
    import services.SessionServiceFormatters._
    Future {
      DB.withSession { implicit s =>
        sessionService.deferCurrentSong(id)
        Ok
      }
    }
  }

  def updateSongStatus(sessionId: SessionId) = Action.async(parse.tolerantJson) { req =>
    import services.SessionServiceFormatters._
    import SessionSongFormatter._
    Future {
      DB.withSession { implicit s =>
        req.body.validate[UpdateSongStatus] match {
          case JsSuccess(message, _) => Ok(Json.toJson(sessionService.updateSongStatus(message)))
          case JsError(errors) => BadRequest(errors.toString)
        }
      }
    }
  }

  def playNow(sessionId: SessionId, songId: SessionSongId) = Action.async { req =>
    import services.SessionServiceFormatters._
    import SessionSongFormatter._
    Future {
      DB.withSession { implicit s =>
        sessionService.playNow(sessionId, songId)
        Ok
      }
    }
  }

  def update(id: SessionId) = DBAction(parse.json) { rs =>
    implicit val session = rs.dbSession

    val existingSession = Json.fromJson[models.Session](rs.body)

    sessionRepository.findById(id) match {
      case Some(_) => existingSession.map(_.copy(id = Some(id))).map(sessionRepository.save).map(sessionRepository.findById) match {
        case JsSuccess(r, _) => Ok(Json.toJson(r))
        case JsError(errors) => UnprocessableEntity(errors.toString())
      }
      case None => NotFound
    }
  }
}