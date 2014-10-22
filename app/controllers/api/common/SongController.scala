package controllers.api.common

import controllers.actions.WithDBSession
import models.SongFormatter._
import play.api.libs.json.Json
import repositories.SongRepositoryComponent
import services.SongServiceComponent
import play.api.mvc._

trait SongController extends Controller with WithDBSession {
  self: SongServiceComponent =>

  def search(term: String) = WithDBSession { implicit dbSession =>
    Action {
      Ok(Json.toJson(songService.searchSongs(term)))
    }
  }
}

object SongController extends SongController with SongServiceComponent with SongRepositoryComponent