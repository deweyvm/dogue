package com.deweyvm.whatever.net

import com.deweyvm.gleany.net.Task
import java.net.{UnknownHostException, Socket}
import com.deweyvm.whatever.Game
import java.io.IOException
import com.deweyvm.whatever.common.Implicits._
import scala.collection.mutable.ArrayBuffer
import com.deweyvm.gleany.Debug
import com.deweyvm.whatever.entities.Code
import com.deweyvm.whatever.common.data.Encoding

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
}

class Client extends Task {
  //result type of actions (success, failure). should probably be Either
  type T = Unit
  val port = 4815
  val address = Game.globals.RemoteIp.getOrElse("localhost")
  var running = true
  var current = ""
  var state:ClientState = Client.State.Connecting
  val buff = new Array[Byte](4096)
  private var client:Option[Socket] = None

  private def tryConnect() {
    def fail(exc:Exception, error:String => ClientError) {
      val stackTrace = exc.getStackTraceString
      Debug.debug("Failure:\n" + stackTrace)
      state = error(stackTrace).toState
      client = None
    }
    try {
      Debug.debug("Attempting to establish a connection to %s" format address)
      client = new Socket(address, 4815).some
      state = Client.State.Connected
      client foreach { sock =>
        sock.setSoTimeout(1000)
      }
      Debug.debug("Success")
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
        val waitMillis = 250
        Thread.sleep(waitMillis)
      }
    }
  }

  def send(message:String):T = {
    tryDo { sock =>
      sock.getOutputStream.write(Encoding.toBytes(message + "\0"))
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
      //todo -- make this a separate function
      val in = sock.getInputStream
      val commands = ArrayBuffer[String]()

      val available = in.available()
      if (available <= 0) {
        return ()
      }
      val bytesRead = in.read(buff, 0, available)

      if (bytesRead > 0) {
        val next = Encoding.fromBytes(buff, bytesRead)
        val lines = next.esplit('\0')
        val last = lines(lines.length - 1)
        val first = lines.dropRight(1)
        for (s <- first) {
          current += s
          commands += current
          current = ""
        }

        commands foreach process

        current = last
      }



    }

  }

  def process(command:String) {
    Debug.debug(command)
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
