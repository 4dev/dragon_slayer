package hammertime

import java.util.concurrent.TimeUnit

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps

class GatlingSimulationTest extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080/")

  val scn = scenario("Wrath of Thor").repeat(1000, "n") {
    exec(
      http("HammerTime")
        .get("hammer/deferred/thor")
        .check(status.is(200))
    ).pause(Duration.apply(5, TimeUnit.MILLISECONDS))
  }

  setUp(scn.inject(atOnceUsers(30))).maxDuration(FiniteDuration.apply(60, "seconds"))
    .protocols(httpProtocol)
}