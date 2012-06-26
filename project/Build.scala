import sbt._
import Keys._

object GatlingBuild extends Build {


    lazy val gatlingProject = Project("gatling-sbt-plugin", file("."), settings=gatlingSettings)
    
    lazy val gatlingSettings = Defaults.defaultSettings/* ++ ScriptedPlugin.scriptedSettings */++ Seq(
        sbtPlugin := true,
        version := "0.0.1-SNAPSHOT",
        organization := "be.nextlab"
    )

   
}

