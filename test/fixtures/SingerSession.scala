package fixtures

import models.Singer

trait SingerSession {
  def withSingerInSession(singer: Singer): (String, String) = ("singerId", singer.id.get.id.toString)
}
