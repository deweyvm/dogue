package com.deweyvm.dogue.net

import com.deweyvm.dogue.common.threading.Task
import com.deweyvm.dogue.Game
import com.deweyvm.dogue.common.logging.Log
import java.io.IOException
import java.net.{SocketException, UnknownHostException}
import com.deweyvm.dogue.common.Implicits._
import com.deweyvm.dogue.common.protocol.DogueMessage
import com.deweyvm.dogue.common.io.DogueSocket
import com.deweyvm.dogue.common.procgen.Name
import com.deweyvm.dogue.ui.TextInput
import com.deweyvm.dogue.common.data.Code

object ClientManager {
  var num = 0
}

class ClientManager(port:Int, host:String) extends Task with Transmitter[DogueMessage] {
  type T = Unit
  Client.name = {
    val u = Game.settings.username.get
    if (u == "") new Name().get else u
  }

  var destName:String = Name.unknown

  override def sourceName = Client.name
  override def destinationName = destName
  private var state:ClientState = Client.State.Connecting
  private var client:Option[Client] = None
  private var killHandshake:Option[() => Unit] = None

  private def tryConnect() {
    def fail(exc:Exception, error:String => ClientError) {
      val stackTrace = exc.getStackTraceString
      Log.warn(Log.formatStackTrace(exc))
      delete(error(stackTrace).toState)
      Thread.sleep(5000)
    }
    try {
      def success(socket:DogueSocket, serverName:String) {
        client = new Client(serverName, socket, this).some
        destName = serverName
        state = Client.State.Connected
        Log.all("Handshake succeeded")
      }
      def fail() {
        delete(Client.Error.ConnectionFailure("Handshake timeout").toState)
      }
      if (state == Client.State.Connecting) {
        val s = "Attempting to establish a connection to %s" format host
        Log.info(s)
        TextInput.putCommand(TextInput.chat, "/local \"%s\"" format s)
        ClientManager.num += 1
        killHandshake = DogueHandshake.begin(host, port, success, fail).some
        state = Client.State.Handshaking
      } else {
        Thread.sleep(100)
      }
      ()
    } catch {
      case ioe:IOException =>
        fail(ioe, Client.Error.ConnectionFailure)
      case uhe:UnknownHostException =>
        fail(uhe, Client.Error.HostUnreachable)
    }
  }

  private def delete(s:ClientState) {
    try {
      Log.info("Deleting client")
      tryMap {_.close()}
      TextInput.putCommand(TextInput.chat, "/local Disconnected")
      killHandshake foreach {_()}
      client = None
      state = s
    } catch {
      case t:Throwable => ()
    }
  }

  override def doWork() {
    if (!client.isDefined) {
      tryConnect()
    }
    tryMap { _.run() }
  }

  override def cleanup() {
    Log.info("ClientManager died")
  }


  override def enqueue(s:DogueMessage) {
    tryMap { _.enqueue(s) }

  }

  override def dequeue:Vector[DogueMessage] = {
    tryMap {_.dequeue} getOrElse Vector()
  }


  def disconnect(reason:ClientState) {
    delete(reason)
  }

  def doTimeout() {
    Log.warn("ping timeout")
    disconnect(Client.Error.Timeout.toState)
  }


  private def tryMap[A](f:Client => A/*,r: A => T*/):Option[A] = {
    import Client.Error._
    try {
      client map f
    } catch {
      case e:SocketException =>
        delete(ConnectionFailure(e.getMessage).toState)
        None
    }
  }

  def sendPing() {
    tryMap(_.sendPing())
  }


  def getStatus:String = {
    import Client.State._
    import Client.Error._
    state match {
      case Offline => "Offline"
      case Connected => Code.☼.rawString
      case Handshaking => "Handshaking..."
      case Connecting =>
        val codes = Vector(Code./, Code.─, Code.backslash, Code.│)
        "Connecting " + codes((Game.getFrame/10) % codes.length).rawString
      case Disconnected(e) => e match {
        case HostUnreachable(msg) => "Server is down (r)" //todo -- put control widget here
        case ConnectionFailure(msg) => "Failed to connect (r)" //todo -- put control widget here
        case Unknown => "Unknown error"
        case Timeout => "Ping timeout"
      }
      case Closed => "Closing..."
    }
  }
}
