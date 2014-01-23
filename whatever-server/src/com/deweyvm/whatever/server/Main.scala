package com.deweyvm.whatever.server

import com.deweyvm.gleany.logging.Logger
import com.deweyvm.whatever.common.Implicits._

object Main {
  def main(args:Array[String]) {
    if (args.contains("--pass")) {
      System.exit(0)
    }
    val logIndex = args.indexOf("--log")
    if (logIndex != -1) {
      val logdir = args(logIndex + 1)
      Server.logdir = logdir.some


    }
    Logger.attachCrasher(false, Server.logdir.getOrElse("."))

    new Server().start()
  }
}
