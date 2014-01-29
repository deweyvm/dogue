package com.deweyvm.dogue.net

import java.net.{InetAddress, NetworkInterface, UnknownHostException, Socket}
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
  }


  val instance = ThreadManager.spawn(new Client())
}

class Client extends Task with Transmitter {
  //result type of actions (success, failure). should probably be Either
  type T = Unit
  val name = createName
  val port = 4815
  val address = Game.globals.RemoteIp.getOrElse("localhost")
  var running = true
  var current = ""
  var state:ClientState = Client.State.Connecting
  val buff = new Array[Byte](4096)
  private var client:Option[Socket] = None

  def getName = name

  private def createName = {
    Game.globals.makeMiniGuid
  }

  private def tryConnect() {
    def fail(exc:Exception, error:String => ClientError) {
      val stackTrace = exc.getStackTraceString
      Log.warn(Log.formatStackTrace(exc))
      state = error(stackTrace).toState
      client = None
      }
    try {
      Log.info("Attempting to establish a connection to %s" format address)
      client = new Socket(address, port).some
      state = Client.State.Connected
      client foreach { sock =>
        sock.setSoTimeout(1000)
      }
      Log.info("Success")
      Thread.sleep(5000)
    } catch {
      case ioe:IOException =>
        fail(ioe, Client.Error.ConnectionFailure)
      case uhe:UnknownHostException =>
        fail(uhe, Client.Error.HostUnreachable)
    }
  }

  def getString:String = {
    import Client.State._
    import Client.Error._
    state match {
      case Offline => "Offline mode"
      case Connected => Code.☼.rawString
      case Connecting =>
        val codes = Vector(Code./, Code.─, Code.\, Code.│)
        "Connecting " + codes((Game.getFrame/10) % codes.length).rawString
      case Disconnected(e) => e match {
        case HostUnreachable(msg) => "Server is down (r)" //todo -- put control widget here
        case ConnectionFailure(msg) => "Failed to connect (r)" //todo -- put control widget here
      }
    }
  }

  override def execute() {
    while (running) {
      if (!client.isDefined && (state == Client.State.Connecting/* || state == Client.State.Disconnected*/)) {
        tryConnect()
      } else {
        read()
        write()
        val waitMillis = 250
        Thread.sleep(waitMillis)
      }
    }
  }

  private val inQueue = new LockedQueue[String]
  private val outQueue = new LockedQueue[String]
  override def enqueue(s:String) {
    Log.info("got command: " + s)
    Log.info("Count before: " + inQueue.length)
    inQueue.enqueue(s)
    Log.info("Cound after: " + inQueue.length)
  }

  override def dequeue:Vector[String] = {
    outQueue.dequeueAll()

  }

  //write all queued messages
  private def write() {
    val toWrite = inQueue.dequeueAll()
    tryDo { sock =>
      toWrite foreach { s =>
        Log.info("Transmitting: \"%s\"" format s)
        sock.transmit(s)
      }
      outQueue enqueueAll toWrite
    }
  }

  def disconnect():T = {
    tryDo { sock =>
      sock.close()
    }
  }

  def isConnected:Boolean = client exists {_.isConnected}

  private def read():T = {
    tryDo { sock:Socket =>
      val read = sock.receive()
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

  }

  def processServerCommand(command:String) {
    Log.info("Processing: " + command)
    outQueue.enqueue(command)
  }

  def getState:ClientState = Client.State.Connecting

  private def tryDo[A](f:Socket => A/*,r: A => T*/):T = {
    client foreach f
  }


  //returns buffered output if any exists
  def getOutput:Vector[String] = {
    Vector()
  }
}
