package controllers

import play.api.mvc._

object StaticController extends Controller {
  def index = Action {
    Ok(views.html.index())
  }
}
