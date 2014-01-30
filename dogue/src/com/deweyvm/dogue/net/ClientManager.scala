package com.deweyvm.dogue.net

import com.deweyvm.dogue.common.threading.Task
import com.deweyvm.dogue.Game
import com.deweyvm.dogue.common.logging.Log
import java.io.IOException
import java.net.{SocketException, UnknownHostException}
import com.deweyvm.dogue.entities.Code
import com.deweyvm.dogue.common.Implicits._

class ClientManager extends Task with Transmitter {
  //result type of actions (success, failure). should probably be Either
  type T = Unit
  private val name = createName
  private var running = true
  private val port = 4815
  private val address = Game.globals.RemoteIp.getOrElse("localhost")
  def getName = name

  private var state:ClientState = Client.State.Connecting
  var client:Option[Client] = None

  private def createName = {
    Game.globals.makeMiniGuid
  }

  private def tryConnect() {
    def fail(exc:Exception, error:String => ClientError) {
      val stackTrace = exc.getStackTraceString
      Log.warn(Log.formatStackTrace(exc))
      delete(error(stackTrace).toState)
      Thread.sleep(5000)
    }
    try {
      state = Client.State.Connecting
      Log.info("Attempting to establish a connection to %s" format address)
      client = new Client(address, port, this).some
      state = Client.State.Connected
      Log.info("Success")
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
      client foreach {_.close()}
      client = None
      state = s
    } catch {
      case t:Throwable => ()
    }
  }

  override def execute() {
    while (running) {
      if (!client.isDefined) {
        tryConnect()
      }
      tryMap { _.run() }
    }
  }


  override def enqueue(s:String) {
    tryMap { _.enqueue(s) }

  }

  override def dequeue:Vector[String] = {
    tryMap {_.dequeue} getOrElse Vector()
  }


  def disconnect() {
    tryMap {_.close()}
    delete(Client.Error.Timeout.toState)
  }

  def doTimeout() {
    Log.warn("ping timeout")
    disconnect()
  }


  def getState:ClientState = Client.State.Connecting

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
      case Offline => "Offline mode"
      case Connected => Code.☼.rawString
      case Connecting =>
        val codes = Vector(Code./, Code.─, Code.\, Code.│)
        "Connecting " + codes((Game.getFrame/10) % codes.length).rawString
      case Disconnected(e) => e match {
        case HostUnreachable(msg) => "Server is down (r)" //todo -- put control widget here
        case ConnectionFailure(msg) => "Failed to connect (r)" //todo -- put control widget here
        case Unknown => "Uknown error"
        case Timeout => "Ping timeout"
      }
    }
  }
}
