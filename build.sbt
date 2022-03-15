import ReleaseTransformations._
import Tests._

name := "processors-models"
organization := "org.clulab"

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.11", "2.12.6")
crossPaths := false // This is a resource only and is independent of Scala version.

// Put these files next to the model, in part so they don't conflict with other dependencies.
mappings in (Compile, packageBin) ++= Seq(
  file("./README.md") -> "README.md",
  file("./CHANGES.md") -> "CHANGES.md",
  file("./LICENSE") -> "LICENSE"
)

resolvers += ("Artifactory" at "http://artifactory.cs.arizona.edu:8081/artifactory/sbt-release").withAllowInsecureProtocol(true)

//
// publishing settings
//
publishArtifact in (Compile, packageBin) := true // Do include the resources.

publishArtifact in (Compile, packageDoc) := false // There is no documentation.

publishArtifact in (Compile, packageSrc) := false // There is no source code.

publishArtifact in (Test, packageBin) := false

// publish to a maven repo
publishMavenStyle := true

// allow overwriting of versions, in case we mess up a release
publishConfiguration := publishConfiguration.value.withOverwrite(true)
publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)

publishTo := {
  val artifactory = "http://artifactory.cs.arizona.edu:8081/artifactory/"
  val repository = "sbt-release-local"
  val details =
      if (isSnapshot.value) ";build.timestamp=" + new java.util.Date().getTime
      else ""
  val location = artifactory + repository + details

  Some(("Artifactory Realm" at location).withAllowInsecureProtocol(true))
}

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
//credentials += Credentials("Artifactory Realm", "artifactory.cs.arizona.edu", "msurdeanu", "X")
// The above credentials are recorded in ~/.sbt/.credentials as such:
// realm=Artifactory Realm
// host=<host>
// user=<user>
// password=<password>

// let’s remove any repositories for optional dependencies in our artifact
pomIncludeRepository := { _ => false }

scmInfo := Some(
  ScmInfo(
    url("https://github.com/clulab/processors-models"),
    "scm:git:https://github.com/clulab/processors-models.git"
  )
)


// mandatory stuff to add to the pom for publishing
pomExtra :=
  <url>https://github.com/clulab/processors-models</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <developers>
    <developer>
      <id>mihai.surdeanu</id>
      <name>Mihai Surdeanu</name>
      <email>mihai@surdeanu.info</email>
    </developer>
  </developers>
//
// end publishing settings
//

// release steps
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  //commitReleaseVersion,
  //tagRelease,
  //releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommandAndRemaining("+publish"),
  setNextVersion,
  //commitNextVersion,
  //releaseStepCommandAndRemaining("sonatypeReleaseAll"),
  //pushChanges
)

