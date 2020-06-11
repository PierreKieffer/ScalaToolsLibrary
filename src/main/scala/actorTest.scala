import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.routing.RoundRobinPool



object actorTest {

  case class payload(s: String, receiver: ActorRef)

  case class splitPayload(
                           data: Array[String]
                         )

  class workerActor extends Actor {
    // workerActor process data and send it to the receiverActor
    def receive = {
      case payload(s, receiver) => {
        Thread.sleep(500)
        receiver ! splitPayload(s.split(","))
        println("Message sent")
      }
      case _ => println("Unknown data type")
    }

  }

  class receiverActor extends Actor {
    // receiverActor print result
    def receive = {
      case splitPayload(a) => {
        println("message received... computing...")
        Thread.sleep(1000)
//        println(a.length)
      }
      case _ => println("Unknown data type")
    }
  }

  class shutDownActor extends Actor {
    // receiverActor print result
    def receive = {
      case splitPayload(a) => {
        println("message received... computing...")
        Thread.sleep(2000)
        println(a(0))
        if (a(0) == "10"){
          context.system.terminate()
        }
      }
      case _ => println("Unknown data type")
    }
  }

  def work = {

    val system = ActorSystem("app")
//    val worker = system.actorOf(RoundRobinPool(2).props(Props[workerActor]), "workerActor")
        val worker = system.actorOf(Props[workerActor], "workerActor")
    val receiver = system.actorOf(Props[receiverActor], "receiverActor")
    val shutdown = system.actorOf(Props[shutDownActor], "shutdownActor")
    for (i <- 1 to 10) {
      worker ! payload( i.toString + "," + "orders,products,posts", receiver)
      worker ! payload( i.toString + "," + "orders,products,posts", shutdown)
    }

  }


  def main(args: Array[String]): Unit = {
//    val t1 = System.nanoTime
    work
//    val duration = (System.nanoTime - t1) / 1e9d
//    println(duration)
  }
}

