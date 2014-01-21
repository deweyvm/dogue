package com.deweyvm.whatever.server

import com.deweyvm.gleany.Debug

class Worker(command:String) extends Runnable {
  override def run() {
    if (command == "/quit") {
      Debug.debug("don't know how to quit :(")
    }
  }
}
