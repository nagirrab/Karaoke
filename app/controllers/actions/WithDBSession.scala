package controllers.actions

import models.{SingerId, Singer}
import play.api.mvc.Security.AuthenticatedBuilder
import play.api.mvc._
import repositories.SingerRepositoryComponent
import play.api.mvc._

import play.api.Play.current

import scala.concurrent.Future
import scala.util.control.Exception._
import play.api.db.slick.{Session => DBSession, _}

trait WithDBSession {
   self: Controller =>

  private var existingSession: Option[DBSession] = None

   def WithDBSession[A](action: DBSession => Action[A]): Action[A] = {
     existingSession match {
       case Some(dbSession) => action(dbSession)
       case None => DB.withSession { dbSession =>
         existingSession = Some(dbSession)
         try {
           action(dbSession)
         } finally {
           existingSession = None
         }
       }
     }
   }
 }