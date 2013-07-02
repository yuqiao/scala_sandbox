import akka.actor._
import java.net.{InetSocketAddress, SocketAddress, URI}
import akka.util.{ ByteString, ByteStringBuilder }

class WebSocketClient( val strUrl:String ) extends Actor {
  import WebSocketFrame._
  val uri = new  URI( strUrl )
  var respondTo  = context.system.actorOf( Props[RespondTo] )   
  var handle =  IOManager( context.system ).connect( new InetSocketAddress( uri.getHost, uri.getPort ) )  
  var handshakeOk = false

  //override def preStart() {  }
  
  def receive = {
    case IO.Read(socket, bytes ) =>
      if( handshakeOk ) {
        respondTo ! bytes 
      }else{
        //TODO: check handshake 
        handshakeOk = true
        println(  "handshake result: \n" + bytes.utf8String )
      }

    case IO.Closed(socket, cause ) => handle = null

    case IO.Connected(h, _ ) => 
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

