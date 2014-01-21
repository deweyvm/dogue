package com.deweyvm.whatever.server

import java.net.{BindException, SocketTimeoutException, Socket, ServerSocket}
import scala.collection.mutable.ArrayBuffer
import com.deweyvm.gleany.Debug
import com.deweyvm.gleany.data.Encoding


class Server extends Task {
  val port = 4815
  var running = true
  var readerId = 0

  override def execute() {
    Debug.debug("Starting server")
    val server = new ServerSocket(port)
    server.setSoTimeout(1000)
    Debug.debug("Server started successfully")
    while(running && !server.isClosed) {
      try {
        val connection = server.accept()
        Debug.debug("Spawning reader: " + readerId)
        readerId += 1
        val reader = new Reader(connection, this)
        reader.run()
      } catch {
        case ste:SocketTimeoutException =>
          Thread.sleep(1000)
      }
    }
    Debug.debug("Shutting down")
  }

}

