import akka.actor._
import java.net.{InetSocketAddress, SocketAddress}
import akka.util.{ ByteString, ByteStringBuilder }

class EchoClient( port: Int ) extends Actor {
  import IO._

  var respondTo: Option [ ActorRef ] = None
  var handle: Option[ SocketHandle ] = None

  override def preStart() {
      respondTo = Some ( context.system.actorOf( Props[RespondTo] )  )
      IOManager( context.system ).connect( new InetSocketAddress("127.0.0.1", port ) )  
  }
  
  def receive = {
    case Read(socket, bytes ) => respondTo.foreach { _ ! bytes }
      
    case Closed(socket, cause ) => handle = None

    case Connected(h, _ ) => 
      println( "Connected!" )
      handle = Some( h )

    case s:String => println("send: " + s ); handle.foreach { _ write  ( ByteString(s ) ++  ByteString("\r\n") ) }

  }
}

case class RespondTo extends Actor {
  def receive = {
    case bytes: ByteString => println( "recv: " + bytes.utf8String  )
  }
}
 
object EchoClient extends App {
 
  val system: ActorSystem = ActorSystem("client")
  val port = Option(System.getenv("SOCKETPORT")).map(_.toInt).getOrElse(7788)
  println( "port:" + port )
  val client = system.actorOf( Props(new EchoClient(port)) )

  while(true){
    Console.readLine() match {
      case s:String => client ! s
    }
  }
}
