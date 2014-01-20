package com.deweyvm.whatever.server

import java.net.{BindException, SocketTimeoutException, Socket, ServerSocket}
import scala.collection.mutable.ArrayBuffer
import com.deweyvm.gleany.Debug


class Server extends Task {
  var running = true
  override def execute() {
    try {
      val server = new ServerSocket(4815)
      server.setSoTimeout(1000)
      while(running && !server.isClosed) {
        try {
          val connection = server.accept()
          val reader = new Reader(connection, this)
          reader.run()
        } catch {
          case ste:SocketTimeoutException => ()
        }

      }
    } catch {
      case e:BindException =>
        Debug.debug("Server already running")
    }
  }
}

