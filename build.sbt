ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  //"-language:postfixOps",
  "-explain",
  "-explain-types",
  "-new-syntax",
  //"-Yexplicit-nulls"
)

//resolvers += Resolver.mavenLocal

// Runtime
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.2" withSources() withJavadoc()
libraryDependencies +=  "org.typelevel" %% "log4cats-slf4j"   % "2.6.0"

libraryDependencies += "org.http4s" %% "http4s-core" % "1.0.0-M29"
libraryDependencies += "org.http4s" %% "http4s-dsl" % "1.0.0-M29"
libraryDependencies += "org.http4s" %% "http4s-blaze-server" % "1.0.0-M29"
libraryDependencies += "org.http4s" %% "http4s-circe" % "1.0.0-M29"

libraryDependencies += "io.circe" %% "circe-core" % "0.14.5"
libraryDependencies += "io.circe" %% "circe-generic" % "0.14.5"
//libraryDependencies += "io.circe" %% "circe-parser" % "0.14.5"

libraryDependencies += "io.github.iltotore" %% "iron" % "2.0.0"
libraryDependencies += "io.github.iltotore" %% "iron-cats" % "2.0.0"
libraryDependencies += "io.github.iltotore" %% "iron-circe" % "2.0.0"

//libraryDependencies += "io.chrisdavenport" %% "http4s-log4cats-contextlog" % "0.3.0"

// Runtime
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.7" % Runtime

// Test
libraryDependencies += "com.disneystreaming" %% "weaver-cats" % "0.8.1" % Test

// Frameworks
testFrameworks += new TestFramework("weaver.framework.CatsEffect")

lazy val root = (project in file("."))
  .settings(
    name := "experiments",
    idePackagePrefix := Some("io.dm")
  )
