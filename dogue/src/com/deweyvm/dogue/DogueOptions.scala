package com.deweyvm.dogue

import com.deweyvm.dogue.common.logging.Log

case class DogueOptions(isDebug:Boolean = false,
                        logLevel:String = "",
                        testsOnly:Boolean = false,
                        failFirst:Boolean = true,
                        version:Boolean = false,
                        headless:Boolean = false)

object OptParser {
  private val parser = new scopt.OptionParser[DogueOptions]("dogue") {
    head("dogue", Game.globals.Version)

    opt[Unit]("test-only") action { (_, c) =>
      c.copy(testsOnly = true)
    } text "run tests then exit"

    opt[String]("log-level") action { (v, c) =>
      c.copy(logLevel = v)
    } text ("log level = {%s}" format Log.levels.map{_.toString.toLowerCase}.mkString(","))

    opt[Unit]("fail-first") action { (_, c) =>
      c.copy(failFirst = true)
    } text "stop testing after the first failure"

    opt[Unit]("debug") action { (_, c) =>
      c.copy(isDebug = true)
    } text "run in debug mode"

    opt[Unit]("version") action { (_, c) =>
      c.copy(version = true)
    } text "show version"

    opt[Unit]("headless") action { (_, c) =>
      c.copy(headless = true)
    }
  }

  /**
   * Throws an exception if options could not be parsed.
   * @return
   */
  def getOptions(args:IndexedSeq[String]):DogueOptions = {
    parser.parse(args, DogueOptions()).getOrElse({
      println(parser.usage)
      Dogue.gdxApp foreach {_.exit()}
      throw new Exception("unreachable")
    })
  }
}

