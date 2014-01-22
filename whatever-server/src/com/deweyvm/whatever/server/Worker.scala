package com.deweyvm.whatever.server

import com.deweyvm.gleany.Debug
import com.deweyvm.gleany.net.Task

class Worker(command:String) extends Task {
  override def execute() {
    if (command == "/quit") {
      Debug.debug("don't know how to quit :(")
    }
  }
}
