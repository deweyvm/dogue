package com.deweyvm.dogue.net

import java.net._
import com.deweyvm.dogue.Game
import java.io.IOException
import com.deweyvm.dogue.common.Implicits._
import scala.collection.mutable.ArrayBuffer
import com.deweyvm.gleany.Debug
import com.deweyvm.dogue.entities.Code
import com.deweyvm.dogue.common.data.{LockedQueue, Encoding}
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.dogue.common.threading.{ThreadManager, Task}
import com.deweyvm.dogue.common.io.{NetworkData, DogueSocket}
import com.deweyvm.dogue.common.protocol.{Invalid, Command, DogueMessage}

trait ClientState
class ClientError(error:String) {
  def toState:ClientState = Client.State.Disconnected(this)
}

object Client {
  object State {
    case object Connecting extends ClientState
    case object Connected extends ClientState
    case object Offline extends ClientState
    case class Disconnected(error:ClientError) extends ClientState
  }

  object Error {
    case class HostUnreachable(error:String) extends ClientError(error)
    case class ConnectionFailure(error:String) extends ClientError(error)
    case object Timeout extends ClientError("Ping timeout")
    case object Unknown extends ClientError("Unknown")
  }


  val instance = ThreadManager.spawn(new ClientManager(Game.globals.getPort, Game.globals.getAddress))
}

class Client(clientName:String, address:String, port:Int, manager:ClientManager) extends Transmitter[DogueMessage] {
  private val waitTimeMillis = 16
  private val socket = DogueSocket.create("unknown", address, port)
  private val pinger:Pinger = ThreadManager.spawn(new Pinger(manager))
  private val readQueue = new LockedQueue[DogueMessage] // read from the server
  private val writeQueue = new LockedQueue[DogueMessage] //to be written to the server
  Log.verbose("test")
  private def read() {
    val commands = socket.receiveCommands()
    commands foreach processServerCommand
  }

  override def getName = clientName

  override def enqueue(s:DogueMessage) {
    Log.info("Got command: " + s)
    writeQueue.enqueue(s)
  }

  override def dequeue:Vector[DogueMessage] = {
    readQueue.dequeueAll()
  }

  def run() {
    read()
    write()
    Thread.sleep(waitTimeMillis)
  }

  private def write() {
    val toWrite = writeQueue.dequeueAll()

    toWrite foreach { s =>
      socket.transmit(s)
    }

  }


  def processServerCommand(command:DogueMessage) {
    command match {
      case Command(op, source, dest, args) =>
        if (op == "pong") {
          pinger.pong()
        } else { //fixme -- pong doesnt get queue'd?
          readQueue.enqueue(command)
        }
      case Invalid(s) => Log.warn("Invalid message received: " + s)
    }
    Log.info("Processing: " + command)

  }

  def sendPing() {
    socket.transmit(Command("ping", clientName, "fixme", Vector()))
  }

  def close() {

  }
}


