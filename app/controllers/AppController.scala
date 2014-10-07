package controllers

import play.api.mvc._

object AppController extends Controller {
  def index = Action {
    Ok(views.html.app())
  }
}
