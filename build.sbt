name := "terasort"

version := "0.0.1"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.2"

//libraryDependencies += "org.apache.flink" %% "flink-clients" % "1.0.3"

// https://mvnrepository.com/artifact/org.apache.flink/flink-streaming-scala_2.10
libraryDependencies += "org.apache.flink" % "flink-streaming-scala_2.10" % "1.0.3"

libraryDependencies += "org.apache.flink" %% "flink-scala" % "1.0.3"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "1.6.2"

fork in run := true
