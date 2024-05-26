ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-explain",
  "-explain-types",
  "-new-syntax",
  //"-Yexplicit-nulls"
)

//resolvers += Resolver.mavenLocal

// Runtime
libraryDependencies += "com.github.pureconfig" %% "pureconfig-core" % "0.17.5"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.2" withSources() withJavadoc()
libraryDependencies += "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.17.6"
libraryDependencies +=  "org.typelevel" %% "log4cats-slf4j"   % "2.6.0"

libraryDependencies += "org.http4s" %% "http4s-core" % "1.0.0-M29" withSources() withJavadoc()
libraryDependencies += "org.http4s" %% "http4s-dsl" % "1.0.0-M29"
libraryDependencies += "org.http4s" %% "http4s-blaze-server" % "1.0.0-M29"
libraryDependencies += "org.http4s" %% "http4s-blaze-client" % "1.0.0-M29"
libraryDependencies += "org.http4s" %% "http4s-circe" % "1.0.0-M29"

libraryDependencies += "org.endpoints4s" %% "algebra" % "1.11.1"

libraryDependencies += "io.circe" %% "circe-core" % "0.14.7"
libraryDependencies += "io.circe" %% "circe-generic" % "0.14.7"
//libraryDependencies += "io.circe" %% "circe-parser" % "0.14.5"

libraryDependencies += "io.github.iltotore" %% "iron" % "2.5.0"
libraryDependencies += "io.github.iltotore" %% "iron-cats" % "2.5.0"
libraryDependencies += "io.github.iltotore" %% "iron-circe" % "2.5.0"

//libraryDependencies += "io.chrisdavenport" %% "http4s-log4cats-contextlog" % "0.3.0"

libraryDependencies += "com.h2database" % "h2" % "2.2.224"
libraryDependencies += "org.flywaydb" % "flyway-core" % "10.11.0"
libraryDependencies += "org.tpolecat" %% "doobie-core" % "1.0.0-M5"
libraryDependencies += "org.tpolecat" %% "doobie-h2" % "1.0.0-M5"
libraryDependencies += "org.tpolecat" %% "doobie-hikari" % "1.0.0-M5"

// Runtime
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.6" % Runtime

// Test
//libraryDependencies += "io.github.dimitarg" %% "weaver-test-extra" % "0.5.8" % Test
libraryDependencies += "com.disneystreaming" %% "weaver-cats" % "0.8.4" % Test

// Frameworks
testFrameworks += new TestFramework("weaver.framework.CatsEffect")

lazy val root = (project in file("."))
  .settings(
    name := "server",
    idePackagePrefix := Some("io.dm")
  )
