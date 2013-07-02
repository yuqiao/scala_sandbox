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
    // TODO: len
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

class WebSocketFrame( data: String, opcode: Byte, fin: Boolean, mask: Boolean)  {

  val maskKey = ByteString( "\nn]2")
  val length_7 = 0x7d
  val length_16 = 1 << 16
  val length_63 = 1 << 63

  def mask( data:String ):ByteString = {
    maskKey ++ ( for( i <- 0 until data.length ) yield { (  data(i) ^ maskKey( i % 4 ) ).asInstanceOf[Byte] } )
  }

  def format():ByteString = {
    val mask_byte =   if( mask ) (1<<7) else 0  
    val head1 = ByteString(  ( if( fin) 1 else 0  ) << 7 | opcode )  
    val len = data.length
    val head2 = {
        if( len < length_7) 
          ByteString(  mask_byte | len ) 
        else if( data.length < length_16 )
          ByteString(  mask_byte | 0x7e )  ++ ByteString ( (len>>8) & 0xff, len & 0xff )
        else
          ByteString(  mask_byte | 0x7f ) ++ ByteString ( (len>>56) &0xff , (len>>48) &0xff, (len>>40)&0xff, (len>>32)&0xff,
              (len>>24) & 0xff, (len>>16) & 0xff, (len>>8) & 0xff,  len & 0xff )
      }
    head1 ++ head2 ++ mask( data )
  }
}


