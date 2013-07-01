import akka.actor._
import akka.dispatch.Promise
import java.net.{InetSocketAddress, SocketAddress}
import sys.ShutdownHookThread
 
object Server extends App {
 
  val system: ActorSystem = ActorSystem("socketserver")
 
  val port = Option(System.getenv("SOCKETPORT")).map(_.toInt).getOrElse(7788)
  val addressPromise = Promise[SocketAddress]()(system.dispatcher)
  val server = system.actorOf(Props(new SocketServer(new InetSocketAddress("localhost", port), addressPromise)))
  addressPromise.map(address => println("Started Socket Server, listening on:" + address))
 
  ShutdownHookThread {
    println("Socket Server exiting...")
    system.shutdown()
    system.awaitTermination()
  }
}

