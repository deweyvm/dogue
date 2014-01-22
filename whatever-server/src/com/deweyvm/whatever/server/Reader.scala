package com.deweyvm.whatever.server

import java.net.Socket
import scala.collection.mutable.ArrayBuffer
import com.deweyvm.gleany.net.Task
import java.io.InputStream
import com.deweyvm.whatever.common.Implicits._
import com.deweyvm.gleany.Debug
import com.deweyvm.whatever.common.data.Encoding


class Reader(socket:Socket, parent:Server) extends Task {
  private var running = true
  private val in: InputStream = socket.getInputStream
  private val inBuffer = ArrayBuffer[String]()
  private var current = ""
  private val buff = new Array[Byte](4096)

  def isRunning:Boolean = running

  def kill() {
    running = false
  }

  override def execute() {
    while(running && !socket.isClosed) {
      val available = in.available()
      if (available > 0) {
        val bytesRead = in.read(buff, 0, available)
        if (bytesRead > 0) {
          val next = Encoding.fromBytes(buff, bytesRead)
          val lines = next.esplit('\0')
          val last = lines(lines.length - 1)
          val first = lines.dropRight(1)
          for (s <- first) {
            current += s
            inBuffer += current
            current = ""
          }
          current = last

          for (s <- inBuffer) {
            new Worker(s, socket/*fixme should probably be another socket?*/).start()
          }
          inBuffer.clear()
        }
      } else {
        Thread.sleep(350)
      }
    }
    running = false
    Debug.debug("Killed")
  }


}


