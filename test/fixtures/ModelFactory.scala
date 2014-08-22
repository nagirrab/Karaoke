package fixtures

import scala.util.Random

/**
 * Created by hugh on 9/8/14.
 */
trait ModelFactory {
  def randomString(length: Int = 10): String = {
    (1 to length).map (_ => Random.nextPrintableChar()).toString()
  }
}
