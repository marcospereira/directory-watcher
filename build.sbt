
organization in ThisBuild := "io.methvin"
licenses in ThisBuild := Seq(
  "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")
)
homepage in ThisBuild := Some(url("https://github.com/gmethvin/directory-watcher"))
scmInfo in ThisBuild := Some(
  ScmInfo(
    url("https://github.com/gmethvin/directory-watcher"),
    "scm:git@github.com:gmethvin/directory-watcher.git"
  )
)
developers in ThisBuild := List(
  Developer("gmethvin", "Greg Methvin", "greg@methvin.net", new URL("https://github.com/gmethvin"))
)

def commonSettings = Seq(
  fork in Test := true,
  javaOptions in Test ++= Seq(
    "-Dorg.slf4j.simpleLogger.defaultLogLevel=debug"
  ),
  libraryDependencies ++= Seq(
    "com.novocode" % "junit-interface" % "0.11" % Test,
    "org.slf4j" % "slf4j-simple" % "1.7.25" % Test,
  )
)

// directory-watcher is a Java-only library. No Scala dependencies should be added.
lazy val `directory-watcher` = (project in file("core"))
  .settings(commonSettings)
  .settings(
    autoScalaLibrary := false,
    crossPaths := false,
    libraryDependencies ++= Seq(
      "net.java.dev.jna" % "jna" % "4.5.2",
      "org.slf4j" % "slf4j-api" % "1.7.25",

      "io.airlift" % "command" % "0.3" % Test,
      "com.google.guava" % "guava" % "25.1-jre" % Test,
      "org.codehaus.plexus" % "plexus-utils" % "3.1.0" % Test,
      "commons-io" % "commons-io" % "2.6" % Test
    )
  )

// directory-watcher-better-files is a Scala library.
lazy val `directory-watcher-better-files` = (project in file("better-files"))
  .settings(commonSettings)
  .settings(
    scalaVersion := "2.12.6",
    crossPaths := true,
    libraryDependencies ++= Seq(
      "com.github.pathikrit" %% "better-files" % "3.6.0",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    )
  )
  .dependsOn(`directory-watcher`)

lazy val root = (project in file("."))
  .settings(
    PgpKeys.publishSigned := {},
    publish := {},
    publishLocal := {},
    publishArtifact := false,
    skip in publish := true
  )
  .aggregate(`directory-watcher`, `directory-watcher-better-files`)

scalafmtOnCompile in ThisBuild := true

publishMavenStyle in ThisBuild := true
publishTo in ThisBuild := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

releaseCrossBuild := true
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommand("publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)
