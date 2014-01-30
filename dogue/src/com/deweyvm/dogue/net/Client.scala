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
    case class Timeout(error:String) extends ClientError(error)
    case object Unknown extends ClientError("Unknown")
  }


  val instance = ThreadManager.spawn(new ClientManager())
}

class Client(address:String, port:Int, manager:ClientManager) extends Transmitter {

  private val socket = new Socket(address, port)
  private var current = ""
  private var pinger:Pinger = ThreadManager.spawn(new Pinger(manager))
  private val readQueue = new LockedQueue[String] // read from the server
  private val writeQueue = new LockedQueue[String] //to be written to the server

  private def read() {
    val read = socket.receive()
    val commands = ArrayBuffer[String]()

    read foreach { next =>
      val lines = next.esplit('\0')
      val last = lines(lines.length - 1)
      val first = lines.dropRight(1)
      for (s <- first) {
        current += s

        commands += current
        Log.warn(current)

        current = ""
      }

      commands foreach processServerCommand

      current = last
    }
  }

  override def enqueue(s:String) {
    Log.info("got command: " + s)
    Log.info("Count before: " + writeQueue.length)
    writeQueue.enqueue(s)
    Log.info("Count after: " + writeQueue.length)
  }

  override def dequeue:Vector[String] = {
    readQueue.dequeueAll()
  }

  def run() {
    read()
    write()
    val waitMillis = 250
    Thread.sleep(waitMillis)
  }

  def isConnected:Boolean = ???

  private def write() {
    val toWrite = writeQueue.dequeueAll()

    toWrite foreach { s =>
      Log.info("Transmitting: \"%s\"" format s)
      socket.transmit(s)
    }

  }


  def processServerCommand(command:String) {
    Log.info("Processing: " + command)
    if (current == "/pong") {
      pinger.pong()
    } else { //fixme -- pong doesnt get queue'd?
      readQueue.enqueue(command)
    }
  }

  def sendPing() {
    socket.transmit("/ping")
  }

  def close() {

  }
}


