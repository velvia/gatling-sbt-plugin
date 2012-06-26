import sbt._
import Keys._

object GatlingPlugin extends Plugin {

    val GatlingTest = config("gatling-test") extend (Test)

    val action: State => State = { state =>
        val extracted: Extracted = Project.extract(state)
        import extracted._

        println("takatak'ing")

        val compileSource = Keys.compile in (currentRef, GatlingTest)

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
                // do something with v: inc.Analysis
                println("should have been compiled...")
                //execute gatling simulations that have just been compiled
                println(v)


                //todo initialize Gatling : drop here what have been done in the play-plugin

                //todo use the test framework for gatling 
                // => simply use 'test' in the current scope/conf (GatlingTest)

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

    val gatlingSettings = inConfig(GatlingTest)(baseGatlingSettings)

    lazy val baseGatlingSettings = Defaults.testSettings ++ Seq(
        galtlingConfFile <<= baseDirectory { _ / "src" / "gatling-test" / "conf" / "galing.conf" },
        sourceDirectories <+= baseDirectory { _ / "src" / "gatling-test" / "simulations" },
        scalaSource <<= baseDirectory { base => base / "src" / "gatling-test" / "simulations" },
        commands ++= Seq(gatlingTakatak)
    )
}