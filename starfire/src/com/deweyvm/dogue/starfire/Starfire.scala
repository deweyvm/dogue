package com.deweyvm.dogue.starfire

import java.net.{SocketTimeoutException, ServerSocket}
import com.deweyvm.dogue.common.Implicits._
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.dogue.common.threading.Task


class Starfire extends Task {
  val port = 4815
  var running = true
  //this doesnt scale to more than 1 concurrent connection
  var currentReader:Option[StarReader] = None
  override def execute() {
    Log.info("Starting server")
    val server = new ServerSocket(port)
    Log.info("Server started successfully")
    while(running && !server.isClosed) {
      Log.info("Awaiting connections")
      val connection = server.accept()
      currentReader foreach {
        Log.info("Killing old reader")
        _.kill()
      }
      Log.info("Spawning reader")
      val reader = new StarReader(connection, this)
      reader.start()
      currentReader = reader.some

    }
    Log.info("Shutting down")
  }

}

