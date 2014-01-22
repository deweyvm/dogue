package com.deweyvm.whatever.server

import com.deweyvm.gleany.Debug
import com.deweyvm.gleany.net.Task
import com.deweyvm.whatever.common.Implicits._
import com.deweyvm.whatever.common.Functions._
import java.net.Socket
import com.deweyvm.whatever.common.data.Encoding

class Worker(string:String, socket:Socket) extends Task {
  override def execute() {
    (doCommand _ ∘ convert)(string)
  }

  private def doCommand(string:String) {
    val parts = string.esplit(' ')
    val command = parts(0)
    val rest = parts.drop(0).mkString(" ")
    if (command == "/quit") {
      Debug.debug("don't know how to quit :(")
    } else if (command == "/say") {
      Debug.debug("saying \"%s\" to the server" format rest)
      socket.getOutputStream.write(Encoding.toBytes(rest + "\0"))
    }
  }

  private def convert(string:String):String = {
    val commandPrefix =
      if (string.length > 0 && string(0) != '/') {
        "/say "
      } else {
        ""
      }
    commandPrefix + string
  }
}
