package command

import java.io.FileReader

import models.Song
import org.supercsv.cellprocessor.ift.CellProcessor
import org.supercsv.io.CsvMapReader
import org.supercsv.prefs.CsvPreference
import repositories.SongRepositoryComponent

import org.virtuslab.unicorn.LongUnicornPlay._
import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import play.api.db.slick.{Session => DBSession, _}

import play.core.StaticApplication

object SongLoader {
  val application = new StaticApplication(new java.io.File("."))

  class SongLoaderImpl extends SongRepositoryComponent {
    def loadSongsFromCsv(path: String = "/Users/hugh/Downloads/karafuncatalog.csv", update: Boolean = false) = {

      val processors: Array[CellProcessor] =  Array(
        new org.supercsv.cellprocessor.constraint.NotNull(), // title
        new org.supercsv.cellprocessor.constraint.NotNull(), // artist
        new org.supercsv.cellprocessor.constraint.NotNull(), // year
        new org.supercsv.cellprocessor.constraint.NotNull(), // duo
        new org.supercsv.cellprocessor.constraint.NotNull(), // explicit
        new org.supercsv.cellprocessor.constraint.NotNull() // date added
      )

      val mapReader = new CsvMapReader(new FileReader(path), new CsvPreference.Builder('"', ';', "\r\n").build)

      val header = mapReader.getHeader(true)

      var dataMap = mapReader.read(header, processors)

      import play.api.Play.current

      DB.withSession {
        implicit dbSession =>


        while(dataMap != null) {
          for(title <- Option(dataMap.get("Title"));
              artist <- Option(dataMap.get("Artist"));
              year <- Option(dataMap.get("Year"));
              duoString <- Option(dataMap.get("Duo"))) yield {
            val duo = duoString == "1"
            val song = Song(title = title.toString, artist = artist.toString, year = Some(year.toString), duo = Option(duo))

            if(update) {
              songRepository.byArtistAndTitleQuery(song.artist, song.title).run.headOption match {
                case Some(existing) => // Do Nothing
                case None => songRepository.save(song)
              }
            } else {
              songRepository.save(song)
            }
          }

          dataMap = mapReader.read(header, processors)
        }
      }
    }
  }

  def run() {
    new SongLoaderImpl loadSongsFromCsv()
  }

}
