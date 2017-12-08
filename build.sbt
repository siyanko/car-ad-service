name := "car-ad-service"

version := "1.0"

scalaVersion := "2.12.4"

enablePlugins(PlayScala)

libraryDependencies ++= Seq (
  guice,
  "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.243",
  "com.typesafe.play" %% "play-json" % "2.6.0"
)

scalacOptions += "-Ypartial-unification"

dynamoDBLocalDownloadDir := file("dynamoDb-local")

startDynamoDBLocal in Test := (startDynamoDBLocal in Test).dependsOn(compile in Test).value
test in Test := (test in Test).dependsOn(startDynamoDBLocal in Test).value
testOnly in Test := (testOnly in Test).dependsOn(startDynamoDBLocal in Test).value
testOptions in Test += (dynamoDBLocalTestCleanup in Test).value
    