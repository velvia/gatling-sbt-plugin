import sbt._
import Keys._

object GatlingBuild extends Build {


    lazy val gatlingProject = Project("gatling-sbt-plugin", file("."), settings=gatlingSettings)

    /* OFFICIAL GATLING REPO */
	val gatlingReleasesRepo = "Gatling Releases Repo" at "http://repository.excilys.com/content/repositories/releases"
	val gatling3PartyRepo = "Gatling Third-Party Repo" at "http://repository.excilys.com/content/repositories/thirdparty"

    /* LOCAL MAVEN REPO */
    val localMavenRepo = "Local Maven Repository" at file(Path.userHome.absolutePath+"/.m2/repository").toURI.toURL.toString

	/* GATLING DEPS */
	val gatlingVersionNumber = "1.2.5"
	val gatlingApp = "com.excilys.ebi.gatling" % "gatling-app" % gatlingVersionNumber //withSources
	val gatlingCore = "com.excilys.ebi.gatling" % "gatling-core" % gatlingVersionNumber //withSources
	val gatlingHttp = "com.excilys.ebi.gatling" % "gatling-http" % gatlingVersionNumber //withSources
	val gatlingRecorder = "com.excilys.ebi.gatling" % "gatling-recorder" % gatlingVersionNumber //withSources
	val gatlingCharts = "com.excilys.ebi.gatling" % "gatling-charts" % gatlingVersionNumber //withSources
	val gatlingHighcharts = "com.excilys.ebi.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersionNumber //withSources

    lazy val libDependencies = Seq(
		/*gatlingApp,
		gatlingCore,
		gatlingHttp,
		gatlingRecorder,
		gatlingCharts,
		gatlingHighcharts*/
		"be.nextlab" %% "gatling-sbt-test-framework" % "0.0.1-SNAPSHOT"
    )

    lazy val gatlingSettings = Defaults.defaultSettings/* ++ ScriptedPlugin.scriptedSettings */++ Seq(
        sbtPlugin := true,
        version := "0.0.1-SNAPSHOT",
        organization := "be.nextlab",

        resolvers ++= Seq(gatlingReleasesRepo, gatling3PartyRepo, localMavenRepo),

	    libraryDependencies ++= libDependencies
    )

   
}

