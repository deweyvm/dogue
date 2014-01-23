package com.deweyvm.whatever.server

import java.net.{SocketTimeoutException, ServerSocket}
import com.deweyvm.gleany.Debug
import com.deweyvm.gleany.net.Task
import com.deweyvm.whatever.common.Implicits._
import java.util.{Date, Calendar}
import java.text.SimpleDateFormat
import java.io.{FileOutputStream, File}
import com.deweyvm.whatever.common.data.Encoding

object Server {
  var logdir:Option[String] = None
  val dateStr = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date)//fixme -- code clone with logger
  val filename = "log_%s" format dateStr
  val logfile = logdir map { f => f + "/" +  filename }  getOrElse ("./" + filename)

  val file = {
    val file = new File(logfile)
    if (!file.exists()) {
      file.createNewFile()
    }
    new FileOutputStream(file, false)
  }
  def log(string: String, stackOffset: Int = 2) {
    val callStack = Thread.currentThread().getStackTrace
    val className = callStack(stackOffset).getClassName.split( """[.]""").last.replace("$", "")
    val s = "%s: %s".format(className, string)
    file.write(Encoding.toBytes(s))
  }
}

class Server extends Task {
  val port = 4815
  var running = true
  var currentReader:Option[Reader] = None
  Server.log("hello")
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

