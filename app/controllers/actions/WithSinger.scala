package controllers.actions

import models.{Singer, SingerId}
import play.api.db.slick.{Session => DBSession}
import play.api.mvc._
import repositories.SingerRepositoryComponent

import scala.concurrent.Future
import scala.util.control.Exception._

trait WithSinger extends WithDBSession {
  self: Controller with SingerRepositoryComponent =>

  def getSingerFromRequest(request: RequestHeader)(implicit dbSession: DBSession) : Option[Singer] =
    for(sidString <- request.session.get("singerId");
        sId <- catching(classOf[NumberFormatException]) opt sidString.toInt;
        s <- singerRepository.findById(SingerId(sId))) yield s


  def WithSinger[A](request: Request[A])(block: (Singer, DBSession) => Result): Result = {
    WithDBSession { implicit dbSession =>
      getSingerFromRequest(request).map(u => block(u, dbSession)).getOrElse(Unauthorized)
    }
  }

}