package controllers.api.singer

import controllers.actions.{WithSinger, WithDBSession}
import models.{SingerFormatter, SessionFormatter, SessionId}
import play.api.db.slick.DBAction
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Controller, Action}
import repositories.SingerRepositoryMessages.{RejoinSessionRequest, SingerRepositoryMessageFormatter, JoinSessionRequest}
import repositories.{SessionSongRepositoryComponent, SessionRepositoryComponent, SingerRepositoryComponent}

import scalaz.{Failure, Success}

trait SessionSingerController extends Controller with WithDBSession with WithSinger {
  self: SingerRepositoryComponent =>

  import SingerRepositoryMessageFormatter._
  import SingerFormatter._

  def join() = WithDBSession { implicit dbSession =>
    Action(parse.tolerantJson) { req =>
      val joinAttempt = Json.fromJson[JoinSessionRequest](req.body)

      joinAttempt.map(a => singerRepository.joinSession(a, None)) match {
        case JsSuccess(Success(s), _) => Ok(Json.toJson(s)).withSession(req.session + ("singerId" -> s.id.get.id.toString()))
        case JsSuccess(Failure(e), _) => BadRequest(e.toString).withSession(req.session - "singerId")
        case JsError(e) => BadRequest(e.toString)
      }
    }
  }

  def rejoin = WithDBSession { implicit dbSession =>
    Action(parse.tolerantJson) { req =>
      val rejoinAttempt = Json.fromJson[RejoinSessionRequest](req.body)

      rejoinAttempt.map(singerRepository.rejoinSession) match {
        case JsSuccess(Success(s), _) => Ok(Json.toJson(s)).withSession(req.session + ("singerId" -> s.id.get.id.toString()))
        case JsSuccess(Failure(e), _) => BadRequest(e.toString).withSession(req.session - "singerId")
        case JsError(e) => BadRequest(e.toString)
      }
    }
  }

  def currentSinger = Action { implicit req =>
    WithSinger(req) { (singer, session) =>
      Ok(Json.toJson(singer))
    }
  }
}

object SessionSingerController
  extends SessionSingerController
  with SingerRepositoryComponent
  with SessionRepositoryComponent
  with SessionSongRepositoryComponent
