name := "logistics"
 
version := "1.0"
 
scalaVersion := "2.10.1"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:reflectiveCalls")
 
resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

resolvers += "taobao mirror" at "http://mvnrepo.taobao.ali.com/mvn/repository"

resolvers += "central" at "http://mvnrepo.taobao.ali.com/mvn/repository"
 
libraryDependencies := Seq(
    "org.scala-lang" % "scala-library" % "2.10.1",
    "com.taobao.tair" % "tair-mc-client" % "1.0.4.8", 
    "log4j" % "log4j" % "1.2.17", 
    "org.springframework" % "spring-core" % "3.0.6.RELEASE", 
    "org.springframework" % "spring-context" % "3.0.6.RELEASE"
)

