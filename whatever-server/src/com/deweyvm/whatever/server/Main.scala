package com.deweyvm.whatever.server

object Main {
  def main(args:Array[String]) {
    if (args.contains("--pass")) {
      System.exit(0)
    }
    new Server().run()
  }
}
