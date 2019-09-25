package utils

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

trait AkkaActor {

  implicit val system = ActorSystem("http-client")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

}
