package controllers.actions

import models.{UserId, User, Singer, SingerId}
import play.api.db.slick.{Session => DBSession}
import play.api.mvc._
import repositories.{UserRepositoryComponent, SingerRepositoryComponent}

import scala.concurrent.Future
import scala.util.control.Exception._

trait WithUser extends WithDBSession {
  self: Controller with UserRepositoryComponent =>

  def getUserFromRequest(request: RequestHeader)(implicit dbSession: DBSession) : Option[User] =
    for(uidString <- request.session.get("userId");
        uId <- catching(classOf[NumberFormatException]) opt uidString.toInt;
        u <- userRepository.findById(UserId(uId))) yield u


  def WithUser[A](request: Request[A])(block: (User, DBSession) => Result): Result = {
    WithDBSession { implicit dbSession =>
      getUserFromRequest(request).map(u => block(u, dbSession)).getOrElse(Unauthorized)
    }
  }

}