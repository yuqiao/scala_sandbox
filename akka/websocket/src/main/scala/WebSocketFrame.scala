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

}

case class WebSocketFrame(
  data: String,
  opcode: Byte = 1,
  fin: Boolean = 1,
  mask: Boolean = 1
)  {
  def format():ByteString {
    val head1 = ByteString(  ( if( fin) 1 else 0  ) << 7 | opcode )  
    val head2 = ByteString(  ( if( mask) 1 else 0 ) << 7  | data.length ) 
    head1 ++ head2
  }
}

