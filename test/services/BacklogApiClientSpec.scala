package services

import com.typesafe.config.ConfigFactory
import models.User
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.{Action, RequestHeader, Results}
import play.api.routing.{Router, SimpleRouter}
import play.api.routing.sird._
import play.api.test.WsTestClient
import play.api.{Application, BuiltInComponentsFromContext, Configuration}
import play.core.server.Server
import play.filters.HttpFiltersComponents
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext

class BacklogApiClientSpec extends PlaySpec with GuiceOneServerPerSuite {

  override def fakeApplication(): Application =
    GuiceApplicationBuilder(
      configuration = Configuration(ConfigFactory.parseResources("dev.conf")),
//      overrides = Seq(
//        bind[ApiDefinitionInitializer].to[ApiDefinitionInitializer.Dev],
//      ),
    )
      .build()

  private implicit val destination: BacklogApiClient.Destination = BacklogApiClient.Destination("example.com", "key")
  private val sampleUser = User(1, "userId", "name")

  private def ifRoute(method: String, path: String)(request: RequestHeader): Boolean =
    request.host == destination.domain && request.method == method

  def withBacklogApiClient[T](block: BacklogApiClient => T): T = {
    Server.withRouter() {
      case request => Action {
        Results.Ok(Json.arr(
          Json.obj(
            "id" -> sampleUser.id,
            "userId" -> sampleUser.userId,
            "name" -> sampleUser.name,
          ),
        ))
      }
    } { implicit port =>
      WsTestClient.withClient { client =>
        val ec = app.injector.instanceOf[ExecutionContext]
        block(new BacklogApiClient(client)(ec))
      }
    }
  }

//  def withBacklogApiClient[T](block: BacklogApiClient => T): T = {
//    Server.withApplicationFromContext() { context =>
//      new BuiltInComponentsFromContext(context) with HttpFiltersComponents {
//        override def router: Router = SimpleRouter({
//          case request => Action {
//            Results.Ok(Json.arr(
//              Json.obj(
//                "id" -> sampleUser.id,
//                "userId" -> sampleUser.userId,
//                "name" -> sampleUser.name,
//              ),
//            ))
//          }
//        })
////        override def router: Router = Router.from {
////          case GET(p"/repositories") => Action {
////            Results.Ok(Json.arr(
////              Json.obj(
////                "id" -> sampleUser.id,
////                "userId" -> sampleUser.userId,
////                "name" -> sampleUser.name,
////              ),
////            ))
////          }
////        }
//      }.application
//    } { implicit port =>
//      WsTestClient.withClient { client =>
//        val ec = app.injector.instanceOf[ExecutionContext]
//        block(new BacklogApiClient(client)(ec))
//      }
//    }
//  }

  "queryProjectUsers" in {
    withBacklogApiClient { client =>
      val users = await(client.queryProjectUsers("1"))
      users mustBe Seq(sampleUser)
    }
  }

}
