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


  def WithSinger[A](action: Singer => Action[A]): Action[A] = {
    WithDBSession { implicit dbSession =>
      val singerBodyParser = parse.using { request =>
        getSingerFromRequest(request).map(u => action(u).parser).getOrElse {
          parse.error(Future.successful(Unauthorized("you must blah blah qqHRB")))
        }
      }

      Action.async(singerBodyParser) { request =>
        getSingerFromRequest(request).map(u => action(u)(request)).getOrElse(Future.successful(Unauthorized))
      }
    }
  }

}