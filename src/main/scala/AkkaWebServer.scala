import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, get, path, post}
import akka.http.scaladsl.server.Route

import scala.io.StdIn

object AkkaWebServer extends utils.AkkaActor with utils.AppConfiguration {

  def initializeWebServer(interface : String, port : Int) = {
    val route: Route =
      concat(
        get{
          path("getData"){
            complete("{\"field1\":\"value\",\"field2\":\"value\",\"field3\":\"value\"}")
          }
        },
        post{
          path("sendMessage"){
            entity(as[String]){message => complete(message)}
          }
        }
      )

    val bindingFuture = Http().bindAndHandle(route, interface, port.toInt)
    println(s"Server online at http://$interface:$port/")

    StdIn.readLine() // Press enter to stop the server
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ ⇒ system.terminate()) // close server
  }


}
