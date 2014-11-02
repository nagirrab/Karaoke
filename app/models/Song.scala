package models

import org.virtuslab.unicorn.LongUnicornPlay._
//import org.virtuslab.unicorn.LongUnicornPlay.driver.simple._
import ExtendedPostgresDriver.simple._
import play.api.libs.json.Json

case class SongId(id: Long) extends BaseId
object SongId extends IdCompanion[SongId]

case class Song(id: Option[SongId] = None, artist: String, title: String, duo: Option[Boolean], year: Option[String]) extends WithId[SongId]

class Songs(tag: Tag)
  extends IdTable[SongId, Song](tag, "songs") {

  def artist = column[String]("artist")
  def title = column[String]("title")
  def duo = column[Option[Boolean]]("duo", O.Nullable)
  def year = column[Option[String]]("year", O.Nullable)


  def * = (id.?, artist, title, duo, year) <> (Song.tupled, Song.unapply)
}

object SongFormatter {
  implicit val songFormat = Json.format[Song]
}