package com.deweyvm.dogue.net

import com.deweyvm.dogue.common.io.DogueSocket
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.dogue.common.threading.Task
import com.deweyvm.dogue.common.protocol.{Invalid, Command}

trait DogueHandshakeState
object DogueHandshake {
  case object Await extends DogueHandshakeState
}
class DogueHandshake(clientName:String, host:String, port:Int, callback:(DogueSocket, String)=>Unit) extends Task {
  import DogueHandshake._
  private var state:DogueHandshakeState = Await
  private val socket = DogueSocket.create("&unknown&", host, port)
  override def init() {
    Log.verbose("Beginning handshake")
  }

  override def doWork() {
    state match {
      case Await =>
        val commands = socket.receiveCommands()
        commands foreach {
          case cmd@Command(op, src, dest, args) =>
            val serverName = src
            if (op == "greet") {
              Log.info("Received greeting from " + serverName)
              socket.transmit(new Command("greet", clientName, serverName, "identify"))
              kill()
              callback(socket, serverName)
            } else {
              Log.warn("Command \"%s\" ignored during handshake." format cmd.toString)
            }

          case Invalid(msg) =>
            Log.warn("Invalid command " + msg)
        }
    }
  }

  override def cleanup() {
    Log.verbose("Dogue handshake dying")
  }

}
