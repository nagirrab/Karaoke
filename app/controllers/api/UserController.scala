package controllers.api

import controllers.actions.{WithUser, WithDBSession, Security}
import play.api.db.slick.DBAction
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import repositories.UserRepositoryComponent
import models.UserFormatter._
import repositories.UserRepositoryMessages._

import scala.concurrent.Future

trait UserController extends Controller with Security with WithDBSession with WithUser {
  self: UserRepositoryComponent =>

  def login = DBAction(parse.tolerantJson) { req =>
    implicit val dbSession = req.dbSession
    val loginAttempt = Json.fromJson[UserLoginAttempt](req.body)

    loginAttempt.map(userRepository.login) match {
      case JsSuccess(Some(u), _) if u.id.nonEmpty => Ok(Json.toJson(u)).withSession(req.session + ("userId" -> u.id.get.id.toString))
      case JsSuccess(_, _) => BadRequest("no such user").withSession(req.session - "userId")
      case JsError(e) => BadRequest(e.toString)

    }
  }

  def logout = Action { req =>
    Ok("logged out").withSession(req.session - "userId")
  }

  def currentUser = Action { req =>
    WithUser(req) { (user, dbSession) =>
      Ok(Json.toJson(user))
    }
  }

  def create = WithDBSession { implicit dbSession =>
    Action(parse.tolerantJson) { req =>
      val creationAttempt = Json.fromJson[UserCreationAttempt](req.body)

      creationAttempt.map(userRepository.create) match {
        case JsSuccess(Right(user), _) => Created(Json.toJson(user))
        case JsSuccess(Left(failure), _) => UnprocessableEntity(Json.toJson(failure.reason))
        case JsError(e) => BadRequest(e.toString())
      }
    }
  }

}

object UserController extends UserController with UserRepositoryComponent
