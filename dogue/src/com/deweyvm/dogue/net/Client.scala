package com.deweyvm.dogue.net

import com.deweyvm.dogue.Game
import com.deweyvm.dogue.common.data.LockedQueue
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.dogue.common.threading.ThreadManager
import com.deweyvm.dogue.common.io.DogueSocket
import com.deweyvm.dogue.common.protocol.{DogueOps, Invalid, Command, DogueMessage}

trait ClientState
class ClientError(error:String) {
  def toState:ClientState = Client.State.Disconnected(this)
}

object Client {
  object State {

    case object Connecting extends ClientState
    case object Handshaking extends ClientState
    case object Connected extends ClientState
    case object Offline extends ClientState
    case object Closed extends ClientState
    case class Disconnected(error:ClientError) extends ClientState
  }

  object Error {
    case class HostUnreachable(error:String) extends ClientError(error)
    case class ConnectionFailure(error:String) extends ClientError(error)
    case object Timeout extends ClientError("Ping timeout")
    case object Unknown extends ClientError("Unknown")
  }

  var name = "&unknown&"
  val instance = ThreadManager.spawn(new ClientManager(Game.globals.getPort, Game.globals.getAddress))
  def isNameGenerated:Boolean = Game.settings.username == null || Game.settings.username == ""
  def setName(name:String) {
    this.name = name
  }
}

class Client(serverName:String, socket:DogueSocket, manager:ClientManager) extends Transmitter[DogueMessage] {
  private val waitTimeMillis = 16

  //private val socket = DogueSocket.create("unknown", address, port)
  private val pinger:Pinger = ThreadManager.spawn(new Pinger(manager))
  private val readQueue = new LockedQueue[DogueMessage] // read from the server
  private val writeQueue = new LockedQueue[DogueMessage] //to be written to the server

  private def read() {
    val commands = socket.receiveCommands()
    commands foreach processServerCommand
  }

  override def sourceName = Client.name
  override def destinationName = serverName

  override def enqueue(s:DogueMessage) {
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
        if (op == DogueOps.Pong) {
          pinger.pong()
        } else { //fixme -- pong doesnt get queue'd?
          readQueue.enqueue(command)
        }
      case Invalid(s, msg) =>
        Log.warn("Invalid message received: %s\n%s" format (s, msg))
    }

  }

  def sendPing() {
    socket.transmit(Command(DogueOps.Ping, Client.name, serverName, Vector()))
  }

  def close() {
    Log.info("Client closing connection")
    socket.transmit(Command(DogueOps.Close, Client.name, serverName, Vector()))
  }
}


