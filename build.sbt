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

//addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

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
//libraryDependencies += "io.github.iltotore" %% "iron-string" % "1.2-1.0.0"
libraryDependencies += "io.github.iltotore" %% "iron-cats" % "2.0.0"
libraryDependencies += "io.github.iltotore" %% "iron-circe" % "2.0.0"

// Test
libraryDependencies += "com.disneystreaming" %% "weaver-cats" % "0.8.1" % Test

// Frameworks
testFrameworks += new TestFramework("weaver.framework.CatsEffect")

lazy val root = (project in file("."))
  .settings(
    name := "experiments",
    idePackagePrefix := Some("io.dm")
  )
