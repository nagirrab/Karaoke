package repositories

import models._
import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import play.api.db.slick.{Session => DBSession}


/**
 * Created by hugh on 9/2/14.
 */

trait SongRepositoryComponent {
  val songRepository = new SongRepository

  // This should really be a trait but unicorn doesn't support it right now.
  // We can still do a mock of the final class instead though.
  class SongRepository extends BaseIdRepository[SongId, models.Song, Songs](TableQuery[Songs]) {

    def byArtistAndTitleQuery(artist: String, title: String) = query.filter(_.artist === artist).filter(_.title === title)

    def fuzzyQuery(term: String) = query.filter { row =>
      (row.artist.toLowerCase startsWith  term.toLowerCase) || (row.title.toLowerCase startsWith term.toLowerCase)
    }
  }
}
