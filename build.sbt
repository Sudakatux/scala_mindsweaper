name := """scala-play-react-seed"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
  watchSources ++= (baseDirectory.value / "public/ui" ** "*").get
)

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += evolutions

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test

libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.2"

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5" //"9.1-901-1.jdbc4
libraryDependencies += "com.h2database" % "h2" % "1.4.197"
