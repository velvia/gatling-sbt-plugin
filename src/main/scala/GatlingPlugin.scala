import sbt._
import Keys._

object GatlingPlugin extends Plugin {
    /* OFFICIAL GATLING REPO */
    val gatlingReleasesRepo = "Gatling Releases Repo" at "http://repository.excilys.com/content/repositories/releases"
    val gatling3PartyRepo = "Gatling Third-Party Repo" at "http://repository.excilys.com/content/repositories/thirdparty"

    /* GATLING DEPS */
    val gatlingVersionNumber = "1.2.5"
    val gatlingApp = "com.excilys.ebi.gatling" % "gatling-app" % gatlingVersionNumber  % "gatling-test"//withSources
    val gatlingCore = "com.excilys.ebi.gatling" % "gatling-core" % gatlingVersionNumber  % "gatling-test"//withSources
    val gatlingHttp = "com.excilys.ebi.gatling" % "gatling-http" % gatlingVersionNumber  % "gatling-test"//withSources
    val gatlingRecorder = "com.excilys.ebi.gatling" % "gatling-recorder" % gatlingVersionNumber  % "gatling-test"//withSources
    val gatlingCharts = "com.excilys.ebi.gatling" % "gatling-charts" % gatlingVersionNumber  % "gatling-test"//withSources
    val gatlingHighcharts = "com.excilys.ebi.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersionNumber  % "gatling-test"//withSources

    val GatlingTest = config("gatling-test") extend (Test)

    val action: State => State = { state =>
        val extracted: Extracted = Project.extract(state)
        import extracted._

        val compileSource = Keys.compile in (currentRef, GatlingTest)

        val launchTest = Keys.test in (currentRef, GatlingTest)


        // Evaluate the task
        // None if the key is not defined
        // Some(Inc) if the task does not complete successfully (Inc for incomplete)
        // Some(Value(v)) with the resulting value
        val result: Option[Result[inc.Analysis]] = Project.evaluateTask(compileSource, state)

        // handle the result
        result match {
            case None => {
                // Key wasn't defined.
                println("cannot find key compile in gatling test configuration")
                state.fail
            }
            case Some(Inc(inc)) => {
                // error detail, inc is of type Incomplete, 
                // use Incomplete.show(inc.tpe) to get an error message
                println(Incomplete.show(inc.tpe))
                state.fail
            }
            case Some(Value(v)) => {
                //execute gatling simulations that have just been compiled
                sys.props += ("sbt.gatling.conf.file" -> extracted.get(galtlingConfFile).getPath)
                sys.props += ("sbt.gatling.result.dir" -> extracted.get(gatlingResultDir).getPath)
                val testsLaunched = Project.evaluateTask(launchTest, state)
                state
            }

            case x => {
                println("Unhandled result while compiling : " + x)
                state.fail
            }


        }

    }

    val gatlingTakatak = Command.command("takatak")(action)

    lazy val galtlingConfFile = SettingKey[File]("gatling-conf-file", "The Gatling-Tool configuration file") in GatlingTest
    lazy val gatlingResultDir = SettingKey[File]("gatling-result-dir", "The Gatling-Tool result dir") in GatlingTest

    val pluginDependencies = Seq(
        gatlingApp,
        gatlingCore,
        gatlingHttp,
        gatlingRecorder,
        gatlingCharts,
        gatlingHighcharts,

        /*todo shouldn't be here...*/
        "be.nextlab" %% "gatling-sbt-test-framework" % "0.0.1-SNAPSHOT" % "gatling-test"
    )

    val gatlingSettings = inConfig(GatlingTest)(baseGatlingSettings)

    val gatlingTestFramework = new TestFramework("be.nextlab.gatling.sbt.plugin.GatlingFramework")


    lazy val baseGatlingSettings = Defaults.testSettings ++ Seq(
        resolvers ++= Seq(gatlingReleasesRepo, gatling3PartyRepo),
        libraryDependencies ++= pluginDependencies,

        galtlingConfFile <<= baseDirectory { _ / "src" / "gatling-test" / "conf" / "galing.conf" },
        gatlingResultDir <<= target { _ / "gatling-test" / "result" },
        sourceDirectories <+= baseDirectory { _ / "src" / "gatling-test" / "simulations" },
        scalaSource <<= baseDirectory { base => base / "src" / "gatling-test" / "simulations" },
        classDirectory <<= (target, scalaVersion) { (t, sv) => t / ("scala-"+sv) / "gatling-test-classes"},
        commands ++= Seq(gatlingTakatak),
        testFrameworks := Seq(gatlingTestFramework),

        //classpathConfiguration := Test,

        logLevel := Level.Debug
    )

}