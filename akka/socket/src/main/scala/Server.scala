import akka.actor._
import java.net.{InetSocketAddress, SocketAddress}
import sys.ShutdownHookThread
 
object Server extends App {
 
  val system: ActorSystem = ActorSystem("socketserver")
 
  val port = Option(System.getenv("SOCKETPORT")).map(_.toInt).getOrElse(7788)
  val server = system.actorOf(Props(new SocketServer(new InetSocketAddress("localhost", port))))
 
  ShutdownHookThread {
    println("Socket Server exiting...")
    system.shutdown()
    system.awaitTermination()
  }
}

