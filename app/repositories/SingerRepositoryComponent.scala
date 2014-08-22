package repositories

import models._
import org.virtuslab.unicorn.UnicornPlay._
import org.virtuslab.unicorn.UnicornPlay.driver.simple._

/**
 * Created by hugh on 9/2/14.
 */

trait SingerRepositoryComponent {
  val singerRepository = new SingerRepository

  // This should really be a trait but unicorn doesn't support it right now.
  // We can still do a mock of the final class instead though.
  class SingerRepository extends BaseIdRepository[SingerId, models.Singer, Singers](TableQuery[Singers]) {
  }
}
