import sbt._
import sbt.Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "comptalib"
  val appVersion      = "1.0"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    //pdf
    "pdf" % "pdf_2.10" % "0.4.1",
    //jsmessages
    "com.github.julienrf" %% "play-jsmessages" % "1.4.1"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    //pdf
    resolvers += Resolver.url("Violas Play Modules", url("http://www.joergviola.de/releases/"))(Resolver.ivyStylePatterns),
    //jsmessages
    resolvers += "julienrf.github.com" at "http://julienrf.github.com/repo/"
  )

}