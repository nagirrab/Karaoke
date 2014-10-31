package controllers.api.singer


import controllers.actions.{WithSinger, WithDBSession}
import models.SessionSongFormatter
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}
import repositories.{SessionRepositoryComponent, SingerRepositoryComponent, SessionSongRepositoryComponent}
import repositories.SessionSongRepositoryMessages.{SessionSongComponentFormatter, RequestSongRequest}

import scalaz._

trait SessionSongController extends Controller with WithDBSession with WithSinger {
  this: SessionSongRepositoryComponent with SingerRepositoryComponent =>

  import SessionSongComponentFormatter._
  import SessionSongFormatter._

  def requestSong() = Action(parse.tolerantJson) { req =>
    WithSinger(req) { (singer, dbSession) =>
      implicit val db = dbSession

      val songRequest = Json.fromJson[RequestSongRequest](req.body)

      songRequest match {
        case JsSuccess(r, p) =>
          sessionSongRepository.requestSong(r, singer) match {
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

}

object SessionSongController
  extends SessionSongController
  with SingerRepositoryComponent
  with SessionSongRepositoryComponent
  with SessionRepositoryComponent
