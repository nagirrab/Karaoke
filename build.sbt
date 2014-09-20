name := """karaoke"""

version := "1.0"

scalaVersion := "2.11.2"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

libraryDependencies ++= Seq(
"org.webjars" % "webjars-play_2.10" % "2.3.0",
  "org.webjars" % "angularjs" % "1.1.5-1",
  "org.webjars" % "bootstrap" % "3.1.1",
  "org.scalatestplus" %% "play" % "1.2.0" % "test",
  "junit" % "junit" % "4.11" % "test",
  "com.novocode" % "junit-interface" % "0.7" % "test->default",
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.postgresql" % "postgresql" % "9.2-1002-jdbc4",
  "org.joda" % "joda-convert" % "1.6",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
  "com.chuusai" %% "shapeless" % "2.0.0",
  "org.virtuslab" %% "unicorn-play" % "0.6.0-M8",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.scalaz" %% "scalaz-core" % "7.1.0"
)

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws
)

javaOptions += "-Dconfig.resource=" + Option(System.getenv("PLAY_ENV")).orElse(Option(System.getProperty("env"))).getOrElse("application") + ".conf"

javaOptions in Test += "-Dconfig.resource=test.conf"