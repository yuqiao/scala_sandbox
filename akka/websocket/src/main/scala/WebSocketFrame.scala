import akka.util.{ ByteString, ByteStringBuilder }

object WebSocketFrame {
  def handshakeFrame( host:String, port:Int, path:String )={
     val handshakeList = List( "GET " + path + " HTTP/1.1",
          "Host: " + host,
          "Upgrade: websocket",
          "Connection: Upgrade",
          "Sec-WebSocket-Key: unAB3PjVQxeI4z+mLVwKzQ==",
          "Origin: http://" + host + ":" + port,
          "Sec-WebSocket-Version: 13",
          "",
          ""
     )
     ByteString(  handshakeList.mkString("\r\n" ) )
  }

  def apply( data:String, opcode: Byte=1, fin:Boolean = true, mask: Boolean = true ):WebSocketFrame={
    new WebSocketFrame( data, opcode, fin, mask )
  }

  def unapply( bytes: ByteString ): Option[ (Int, String) ] = {
    val b1 = bytes( 0 )
    val fin:Boolean  =  if( (b1>>7) == 1 ) true else false
    val opcode:Byte  =  ( b1 & 0xf ).asInstanceOf[Byte]
    val b2 =  bytes(1)
    val mask = if(  ( b2 >>7 ) == 1) true else false
    val len = b2 & 0x7f 
    if(mask){ 
      //TODO: ...
       None
    }else{
       val data =  bytes.slice( 2, 2 + len ).utf8String       
       Some( opcode, data )
    }
  }
}

class WebSocketFrame(
  data: String,
  opcode: Byte = 1,
  fin: Boolean = true,
  mask: Boolean = true
)  {

  val maskKey = ByteString( "\nn]2")

  def mask( data:String ):ByteString = {
    val res = for( i <- 0 until data.length ) yield { (  data(i) ^ maskKey( i % 4 ) ).asInstanceOf[Byte] }
    return maskKey  ++  res 
  }

  def format():ByteString = {
    val head1 = ByteString(  ( if( fin) 1 else 0  ) << 7 | opcode )  
    val head2 = ByteString(  ( if( mask) 1 else 0 ) << 7  | data.length ) 
    head1 ++ head2 ++ mask( data )
  }
}


