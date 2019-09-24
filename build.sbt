name := "ScalaToolsLibrary"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.3"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.3",
  "com.typesafe.akka" %% "akka-stream" % "2.5.12",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.3",
  "io.spray" %% "spray-json" % "1.3.4")


libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.3.1"
libraryDependencies += "org.yaml" % "snakeyaml" % "1.18"

// https://mvnrepository.com/artifact/com.lihaoyi/requests
libraryDependencies += "com.lihaoyi" %% "requests" % "0.1.9"

// https://mvnrepository.com/artifact/io.spray/spray-http
libraryDependencies += "io.spray" %% "spray-http" % "1.3.4"

// https://mvnrepository.com/artifact/com.google.cloud.bigtable/bigtable-hbase-1.x
libraryDependencies += "com.google.cloud.bigtable" % "bigtable-hbase-1.x" % "1.11.0"




assemblyMergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$") => MergeStrategy.discard
  case "log4j.properties" => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") =>
    MergeStrategy.filterDistinctLines
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}
