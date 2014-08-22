package controllers.api

import controllers.actions.{WithDBSession, Security}
import models.UserLoginAttempt
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import repositories.UserRepositoryComponent
import models.UserFormatter._


object UserController extends Controller with Security with WithDBSession {
  self: UserRepositoryComponent =>

  def login = WithDBSession { dbSession =>
    Action(parse.tolerantJson) { req =>
      val loginAttempt = Json.fromJson[UserLoginAttempt](req.body)

      loginAttempt match {
        case JsSuccess(u, _) => Ok("logged in").withSession(req.session + ("username" -> u.email))
        case JsError(e) => BadRequest(e.toString)
      }
    }
  }

  def logout = Action { req =>
    Ok("logged out").withSession(req.session - "username")
  }


}
