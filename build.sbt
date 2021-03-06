name := "java-scala-utils-converter"
organization := "org.dmonix.functional"
version := "1.1-SNAPSHOT"

scalaVersion := "2.11.7"
crossScalaVersions := Seq("2.11.7", "2.12.4")


scalacOptions <++= scalaVersion map { (v: String) => 
  if (v.trim.startsWith("2.1"))
    Seq("-deprecation", "-unchecked", "-feature", "-language:implicitConversions", "-language:higherKinds", "-target:jvm-1.8")
  else
    Seq("-deprecation", "-unchecked")
}

scalacOptions in (Compile, doc) ++= Seq("-doc-title", "Java Scala Utils Converter API")
scalacOptions in (Compile, doc) ++= Seq("-doc-root-content", baseDirectory.value+"/src/main/scaladoc/root-doc.txt")
scalacOptions in (Compile, doc) ++= Seq("-doc-footer", "Copyright (c) 2015 Peter Nerg, Apache License v2.0.")

libraryDependencies ++= Seq(
  "org.dmonix.functional" % "java-scala-utils" % "1.5",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)

//sbt-coverage settings
//ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := {
//  if (scalaBinaryVersion.value == "2.10") false
//  else false  // WTF?
//}

//setting for eclipse plugin to download sources
EclipseKeys.withSource := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.endsWith("-SNAPSHOT"))
    Some("snapshots" at nexus+"content/repositories/snapshots")
  else
    Some("releases" at nexus+"service/local/staging/deploy/maven2")
}



//----------------------------
//info for where and how to publish artifacts
//----------------------------
credentials ++= {
  val sonatype = ("Sonatype Nexus Repository Manager", "oss.sonatype.org")
  def loadMavenCredentials(file: java.io.File) : Seq[Credentials] = {
    xml.XML.loadFile(file) \ "servers" \ "server" map (s => {
      val host = (s \ "id").text
      val realm = if (host == sonatype._2) sonatype._1 else "Unknown"
      Credentials(realm, host, (s \ "username").text, (s \ "password").text)
    })
  }
  val ivyCredentials   = Path.userHome / ".ivy2" / ".credentials"
  val mavenCredentials = Path.userHome / ".m2"   / "settings.xml"
  (ivyCredentials.asFile, mavenCredentials.asFile) match {
    case (ivy, _) if ivy.canRead => Credentials(ivy) :: Nil
    case (_, mvn) if mvn.canRead => loadMavenCredentials(mvn)
    case _ => Nil
  }
}

//----------------------------
//needed to create the proper pom.xml for publishing to mvn central
//----------------------------
publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }
pomExtra := (
  <url>https://github.com/pnerg/java-scala-util-converter</url>
  <licenses>
    <license>
      <name>Apache</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:pnerg/java-scala-util-converter.git</url>
    <connection>scm:git:git@github.com:pnerg/java-scala-util-converter.git</connection>
  </scm>
  <developers>
    <developer>
      <id>pnerg</id>
      <name>Peter Nerg</name>
      <url>http://github.com/pnerg</url>
    </developer>
  </developers>)
