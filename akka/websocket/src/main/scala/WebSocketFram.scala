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
