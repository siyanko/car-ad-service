name := "car-ad-service"

version := "1.0"

scalaVersion := "2.12.4"

enablePlugins(PlayScala)

libraryDependencies ++= Seq (
  guice,
  "org.scalaz" %% "scalaz-core" % "7.2.16",
  "org.scalaz" %% "scalaz-effect" % "7.2.16",
  "org.scalaz" %% "scalaz-concurrent" % "7.2.16"
)
    