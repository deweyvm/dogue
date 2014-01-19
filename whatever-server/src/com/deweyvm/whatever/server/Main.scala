package com.deweyvm.whatever.server

object Main {
  def main(args:Array[String]) {
    new Thread(new Server).start()
  }
}
