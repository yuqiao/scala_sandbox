import java.net._
import akka.actor._
import akka.util.{Timeout, ByteString, ByteStringBuilder}
import akka.dispatch.Promise

object SocketConstants {
  val EOL = ByteString("\r\n")
}

class SocketServer(address: InetSocketAddress = new InetSocketAddress("localhost", 0), addressPromise: Promise[SocketAddress]) extends Actor {               
 
  import SocketConstants._

  val state = IO.IterateeRef.Map.async[IO.Handle]()(context.dispatcher)
  val server = IOManager(context.system) listen (address)
 
  override def postStop() {
    server.close()
    state.keySet foreach (_.close())
  }
 
  def receive = {
    case Timeout =>
      postStop()
 
    case IO.Listening(server, address) =>
      addressPromise.success(address)
 
    case IO.NewClient(server) =>
      val socket = server.accept()
      socket.write(ByteString("Welcome!") ++ EOL ++ ByteString("This is an Echo server.") ++ EOL)
      state(socket) flatMap (_ => SocketServer.processRequest(socket))
 
    case IO.Read(socket, bytes) => state(socket)(IO.Chunk(bytes))
 
    case IO.Closed(socket, cause) =>
      state(socket)(IO.EOF(None))
      state -= socket
  }
}

object SocketServer {
 
  import SocketConstants._
  def processRequest(implicit socket: IO.SocketHandle): IO.Iteratee[Unit] = {
    IO.repeat {
      for {
        all <- IO takeUntil EOL
      } yield {
        if( all.length > 0 ){ 
          println("Echo: " + all.utf8String)
          socket.write(all ++ EOL)
        }
      } 
    }
  }
}

