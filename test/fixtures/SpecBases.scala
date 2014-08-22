package fixtures

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.{WsScalaTestClient, OneAppPerSuite}
import org.specs2.mock.Mockito
import play.api.db.slick._
import play.api.test.{FakeApplication, PlayRunners}

/**
 * Created by hugh on 9/2/14.
 */

/**
 * Helper trait to provide common styles and helpers
 */
trait SpecBase extends WordSpec with Matchers with OptionValues with WsScalaTestClient with Mockito with OneAppPerSuite with ScalaFutures with BeforeAndAfterEach with ModelFactory

trait DBSpecBase extends WordSpec with Matchers with OptionValues with WsScalaTestClient with Mockito with ScalaFutures with BeforeAndAfterEach with PlayRunners with OneAppPerSuite with ModelFactory {
  // Override the default app per suite to have a fake in memory database
  override implicit lazy val app = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))

  /**
   * Trivial wrapper to provide a rollback after every test case. Or if you prefer, use one app per test to get a new database each time.
   * @param f function to run
   * @tparam T return value of function
   * @return the output of your function
   */
  def withRollback[T](f: Session => T): T = {
    implicit val session = DB.createSession()
    session.conn.setAutoCommit(false)

    try {
      f(session)
    } finally {
      session.rollback()
      session.close()
    }
  }
}
