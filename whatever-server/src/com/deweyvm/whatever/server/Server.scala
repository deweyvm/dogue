package com.deweyvm.whatever.server

import java.net.{SocketTimeoutException, ServerSocket}
import com.deweyvm.gleany.Debug
import com.deweyvm.gleany.net.Task
import com.deweyvm.whatever.common.Implicits._


class Server extends Task {
  val port = 4815
  var running = true
  var currentReader:Option[Reader] = None

  override def execute() {
    Debug.debug("Starting server")
    val server = new ServerSocket(port)
    server.setSoTimeout(1000)
    Debug.debug("Server started successfully")
    while(running && !server.isClosed) {
      try {
        val connection = server.accept()
        currentReader foreach {
          Debug.debug("Killing old reader")
          _.kill()
        }
        Debug.debug("Spawning reader")
        val reader = new Reader(connection, this)
        reader.start()
        currentReader = reader.some
      } catch {
        case ste:SocketTimeoutException =>
          Thread.sleep(100)
      }
    }
    Debug.debug("Shutting down")
  }

}

