import akka.actor._
import java.net.{InetSocketAddress, SocketAddress}
import akka.util.{ ByteString, ByteStringBuilder }

class EchoClient( val port:Int ) extends Actor {
  import IO._

  var respondTo:  ActorRef  = _
  var handle: SocketHandle  = _

  override def preStart() {
      respondTo =  context.system.actorOf( Props[RespondTo] )  
      handle = IOManager( context.system ).connect( new InetSocketAddress( "127.0.0.1", port ) )  
  }
  
  def receive = {
    case Read(socket, bytes ) => respondTo ! bytes 
      
    case Closed(socket, cause ) => handle = null

    case Connected(h, _ ) => 
      println( "Connected!" )
      handle =  h 

    case s:String => println("send: " + s ); handle.write ( ByteString(s ) ++  ByteString("\r\n") ) 

  }
}

case class RespondTo extends Actor {
  def receive = {
    case bytes: ByteString => println( "recv: " + bytes.utf8String  )
  }
}
 
object EchoClient extends App {
 
  val system: ActorSystem = ActorSystem("client")
  val port = Option(System.getenv("URI")).map(_.toInt).getOrElse(7788)
  println( "port:" + port )
  val client = system.actorOf( Props(new EchoClient(port)) )

  while(true){
    Console.readLine() match {
      case s:String => client ! s
    }
  }
}

