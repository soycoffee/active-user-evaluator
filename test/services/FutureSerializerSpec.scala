package services

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.test.Helpers._
import test.helpers.NoSlick

import scala.concurrent.Future
import scala.concurrent.duration._

class FutureSerializerSpec extends PlaySpec with GuiceOneServerPerSuite with NoSlick {

  import scala.concurrent.ExecutionContext.Implicits.global

  "apply" in {
    val futureSerializer = app.injector.instanceOf[FutureSerializer]
    val start = System.currentTimeMillis()
    val times = await(futureSerializer[Int, Long]((0 to 3).toList)(_ => Future(System.currentTimeMillis()))(Some(1.millis)))
    val intervals = for (Seq(a, b) <- (Seq(start) ++ times).sliding(2)) yield b - a
    intervals foreach { interval =>
      assert(interval >= 1)
    }
  }

}
