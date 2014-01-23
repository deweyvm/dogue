package com.deweyvm.whatever.server

import com.deweyvm.gleany.logging.Logger
import com.deweyvm.whatever.common.Implicits._
import com.deweyvm.whatever.common.logging.Log

object Main {
  def main(args:Array[String]) {
    if (args.contains("--pass")) {
      System.exit(0)
    }
    val logIndex = args.indexOf("--log")
    val logDir =
      if (logIndex != -1) {
        args(logIndex + 1)
      } else {
        "."
      }
    Log.setDirectory(logDir)

    new Server().start()
  }
}
