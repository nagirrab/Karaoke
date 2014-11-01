package controllers.api.common

import controllers.actions.WithDBSession
import models.SongFormatter._
import play.api.db.slick.DBAction
import play.api.libs.json.Json
import repositories.SongRepositoryComponent
import services.SongServiceComponent
import play.api.mvc._

trait SongController extends Controller {
  self: SongServiceComponent =>

  def search(term: String) = DBAction { req =>
    implicit val dbSession = req.dbSession
    Ok(Json.toJson(songService.searchSongs(term)))
  }
}

object SongController extends SongController with SongServiceComponent with SongRepositoryComponent