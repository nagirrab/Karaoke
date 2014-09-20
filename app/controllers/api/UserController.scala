package controllers.api

import controllers.actions.{WithDBSession, Security}
import models.UserLoginAttempt
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import repositories.UserRepositoryComponent
import models.UserFormatter._


trait UserController extends Controller with Security with WithDBSession {
  self: UserRepositoryComponent =>

  def login = WithDBSession { implicit dbSession =>
    Action(parse.tolerantJson) { req =>
      val loginAttempt = Json.fromJson[UserLoginAttempt](req.body)

      loginAttempt.map(userRepository.login) match {
        case JsSuccess(Some(u), _) if u.id.nonEmpty => Ok("logged in").withSession(req.session + ("userId" -> u.id.get.id.toString))
        case JsSuccess(_, _) => BadRequest("no such user").withSession(req.session - "userId")
        case JsError(e) => BadRequest(e.toString)
      }
    }
  }

  def logout = Action { req =>
    Ok("logged out").withSession(req.session - "userId")
  }


}

object UserController extends UserController with UserRepositoryComponent
