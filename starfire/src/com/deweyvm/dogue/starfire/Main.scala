package com.deweyvm.dogue.starfire

import com.deweyvm.dogue.common.logging.Log

case class StarfireOptions(logDir:String=".")


object Main {
  def main(args:Array[String]) {


    val parser = new scopt.OptionParser[StarfireOptions]("starfire") {
      head("starfire", "testing.0")

      opt[String]("log") action { (x, c) =>
        c.copy(logDir = x)
      } text "directory to place logs"

    }
    parser.parse(args, StarfireOptions()) map { c =>
      Log.setDirectory(c.logDir)
      new Starfire().start()
    } getOrElse {
      println(parser.usage)
      throw new RuntimeException("invalid args")
    }

  }
}
