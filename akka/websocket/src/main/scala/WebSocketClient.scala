import akka.actor._
import java.net.{InetSocketAddress, SocketAddress, URI}
import akka.util.{ ByteString, ByteStringBuilder }

class WebSocketClient( val strUrl:String ) extends Actor {
  import IO._
  import WebSocketFrame._
  val uri = new  URI( strUrl )
  var respondTo:  ActorRef  = _
  var handle: SocketHandle  = _
  var handshakeOk = false

  override def preStart() {
      respondTo =  context.system.actorOf( Props[RespondTo] )  
      handle = IOManager( context.system ).connect( new InetSocketAddress( uri.getHost, uri.getPort ) )  
  }
  
  def receive = {
    case Read(socket, bytes ) =>
      if( handshakeOk ) {
        respondTo ! bytes 
      }else{
        //TODO: check handshake 
        handshakeOk = true
        println(  "handshake result: \n" + bytes.utf8String )
      }

    case Closed(socket, cause ) => handle = null

    case Connected(h, _ ) => 
      println( "Connected!" ) 
      handle.write( handshakeFrame( uri.getHost, uri.getPort, uri.getPath ) )
    
    case s:String => 
      println("send: " + s ); 
      val frame = WebSocketFrame( s ) 
      handle.write ( frame.format ) 
  }
}

case class RespondTo extends Actor {
  def receive = {
    case WebSocketFrame( opcode, data ) => println( "recv1: " + data )
    case bytes: ByteString => println( "recv2: " + bytes  )
  }
}
 
object WebSocketClient extends App {
  val system = ActorSystem("websocketclient")
  val uri =  "ws://127.0.0.1:8888/echo"
  val client = system.actorOf( Props(new WebSocketClient(uri)) )

  while(true){
    Console.readLine() match {
      case s:String => client ! s
    }
  }
}

