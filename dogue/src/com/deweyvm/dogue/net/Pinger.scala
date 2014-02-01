package com.deweyvm.dogue.net

import com.deweyvm.dogue.Game
import com.deweyvm.dogue.common.threading.Task
import com.deweyvm.dogue.common.logging.Log

class Pinger(clientManager:ClientManager) extends Task {
  private var lastPongReceived = Game.getFrame
  private var lastPingSent = Game.getFrame
  private val pingFrequency = 90*60
  private val maxPongFrames = 120*60
  private val checkFrequency = 350
  override def doWork() {
    Thread.sleep(checkFrequency)
    if (Game.getFrame - lastPingSent > pingFrequency) {
      Log.all("Sending ping " + Game.getFrame)
      lastPingSent = Game.getFrame
      clientManager.sendPing()
    }
    if (Game.getFrame - lastPongReceived > maxPongFrames) {
      clientManager.doTimeout()
      Log.info("Ping timeout %d seconds" format (maxPongFrames/60))
      kill()
    }
  }

  override def cleanup() {
    Log.all("Pinger shutting down")
  }

  def pong() {
    lastPongReceived = Game.getFrame
  }
}
