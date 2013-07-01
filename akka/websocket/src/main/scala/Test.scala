import  akka.util.ByteString

object Test extends App {
  val b1 = ByteString("123")
  val b2 = ByteString( 123 )
  println( b1 )
  println( b2 )

}
