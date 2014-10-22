package services

import models.{SessionSongOrder, SessionSong, SessionSongOrders, SessionId}
import play.api.db.slick.{Session => DBSession}
import repositories.{SongRepositoryComponent, SessionSongRepositoryComponent, SessionRepositoryComponent}
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._

import scala.slick.lifted.TableQuery

trait SongServiceComponent {
  this: SongRepositoryComponent =>

  val songService: SongService = new SongService

  class SongService {

    def searchSongs(term: String)(implicit dbSession: DBSession) = {
      songRepository.fuzzyQuery(term).take(10).list
    }
  }
}
