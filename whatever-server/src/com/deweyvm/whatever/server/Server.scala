package com.deweyvm.whatever.server

import java.net.{SocketTimeoutException, Socket, ServerSocket}
import scala.collection.mutable.ArrayBuffer


class Server extends Runnable {
  val server = new ServerSocket(4815)
  var running = true
  override def run() {
    server.setSoTimeout(1000)
    while(running && !server.isClosed) {
      try {
        val connection = server.accept()
        val reader = new Reader(connection, this)
        new Thread(reader).start()
      } catch {
        case ste:SocketTimeoutException => ()
      }

    }
  }
}

