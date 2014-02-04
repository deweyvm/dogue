package com.deweyvm.dogue.net

import com.deweyvm.dogue.common.io.DogueSocket
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.dogue.common.threading.Task
import com.deweyvm.dogue.common.protocol.{DogueOps, Command}
import com.deweyvm.dogue.Game
import com.deweyvm.dogue.common.procgen.Name

trait DogueHandshakeState
object DogueHandshake {
  type SuccessCallback = (DogueSocket, String)=>Unit
  type FailureCallback = () => Unit
  /**
   *
   * @return a function that will close the handshake in the event of a failure
   */
  def begin(host:String, port:Int, success:SuccessCallback, failure:FailureCallback):() => Unit = {
    val socket =  DogueSocket.create(Name.unknown, host, port)
    val result = new AwaitGreeting(socket, success, failure)
    result.start()
    result.kill
  }

  class AwaitGreeting(socket:DogueSocket, success:SuccessCallback, failure:FailureCallback) extends Task {
    override def init() {
      Log.info("Awaiting greeting")
    }
    var iters = 10
    override def doWork() {
      socket.receiveCommands() foreach { case cmd@Command(op, src, dest, args) =>
        op match {
          case DogueOps.Greet =>
            new Identify(socket, src, success, failure).start()
            kill()
          case _ => //todo, queue command
        }
      }
      Thread.sleep(500)
      iters -= 1
      if (iters <= 0) {
        Log.warn("Handshake timeout")
        failure()
        kill()
      }
    }

    override def exception(t:Throwable) {
      failure()
    }
  }

  class Identify(socket:DogueSocket, serverName:String, success:SuccessCallback, failure:FailureCallback) extends Task {
    override def doWork() {
      Log.info("Identifying")
      if (Client.isNameGenerated) {
        socket.transmit(Command(DogueOps.Greet, Client.name, serverName, Vector()))
      } else {
        socket.transmit(new Command(DogueOps.Identify, Client.name, serverName, Client.name, Game.settings.password.get))
      }
      Log.info("Done identifying")
      kill()
      success(socket, serverName)
    }

    override def exception(t:Throwable) {
      failure()
    }

  }
}
