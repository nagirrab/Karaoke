package controllers.api.singer


import controllers.actions.{WithSinger, WithDBSession}
import models.{SessionSongId, SongId, SessionSongFormatter}
import play.api.db.slick.DBAction
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}
import repositories.{SessionRepositoryComponent, SingerRepositoryComponent, SessionSongRepositoryComponent}
import repositories.SessionSongRepositoryMessages.{GuestRequestSongRequest, SessionSongComponentFormatter, RequestSongRequest}
import services.SessionServiceComponent

import scalaz._

trait SessionSongController extends Controller with WithDBSession with WithSinger {
  this: SessionSongRepositoryComponent with SessionServiceComponent with SingerRepositoryComponent =>

  import SessionSongComponentFormatter._
  import SessionSongFormatter._

  def guestRequestSong() = DBAction(parse.tolerantJson) { req =>
    implicit val db = req.dbSession

    val songRequest = Json.fromJson[GuestRequestSongRequest](req.body)

    songRequest match {
      case JsSuccess(r, p) =>
        sessionService.guestRequestSong(r) match {
          case Success(song) => Created(Json.toJson(song))
          case Failure(error) => BadRequest(error.toString)
        }
      case JsError(e) => BadRequest(e.toString())
      case _ => BadRequest("wtf")
    }
  }

  def requestSong() = Action(parse.tolerantJson) { req =>
    WithSinger(req) { (singer, dbSession) =>
      implicit val db = dbSession

      val songRequest = Json.fromJson[RequestSongRequest](req.body)

      songRequest match {
        case JsSuccess(r, p) =>
          sessionService.requestSong(r, singer) match {
            case Success(song) => Created(Json.toJson(song))
            case Failure(error) => BadRequest(error.toString)
          }
        case JsError(e) => BadRequest(e.toString())
        case _ => BadRequest("wtf")
      }
    }
  }

  def activeSongs() = Action { req =>
    WithSinger(req) { (singer, dbSession) =>
      implicit val db = dbSession
      Ok(Json.toJson(sessionSongRepository.activeSongsBySinger(singer.id.get)))
    }
  }

  def completedSongs() = Action { req =>
    WithSinger(req) { (singer, dbSession) =>
      implicit val db = dbSession
      Ok(Json.toJson(sessionSongRepository.completedSongsBySinger(singer.id.get)))
    }
  }

  def cancel(songId: SessionSongId) =  Action { req =>
    WithSinger(req) { (singer, dbSession) =>
      implicit val dbs = dbSession
      sessionSongRepository.cancelSong(songId, singer.id.get) match {
        case Success(song) => Ok(Json.toJson(song))
        case Failure(error) => BadRequest(error.toString)
      }
    }

  }

}

object SessionSongController
  extends SessionSongController
  with SingerRepositoryComponent
  with SessionSongRepositoryComponent
  with SessionRepositoryComponent
  with SessionServiceComponent
