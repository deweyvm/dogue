package com.deweyvm.whatever.server

class Worker(command:String, close:() => Unit) extends Runnable {
  override def run() {
    if (command == "/quit") {
      close()
    }
  }
}
