package controllers.actions

/**
 * Created by hugh on 8/26/14.
 */
trait Security {

 //def getUserFromRequest(request: RequestHeader) = request.session.get("username").map(u => User(Some(1), u, "blah"))


//  case class AuthenticatedDbRequest[A](user: User,
//                                  dbSession: JdbcBackend.Session,
//                                  request: Request[A]) extends WrappedRequest[A](request)
//
//  object Authenticated extends ActionBuilder[AuthenticatedDbRequest] {
//    def invokeBlock[A](request: Request[A], block: (AuthenticatedDbRequest[A]) => Future[Result]) = {
//      AuthenticatedBuilder(req => getUserFromRequest(req)).authenticate(request, { authRequest: AuthenticatedRequest[A, User] =>
//        DB.withSession { implicit session =>
//          block(new AuthenticatedDbRequest[A](authRequest.user, session, request))
//        }
//      })
//    }
//  }
}
