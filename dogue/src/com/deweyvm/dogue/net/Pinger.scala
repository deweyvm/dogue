package com.deweyvm.dogue.net

import com.deweyvm.dogue.Game
import com.deweyvm.dogue.common.threading.Task
import com.deweyvm.dogue.common.logging.Log

class Pinger(client:Client) extends Task {
  private var lastPongReceived = Game.getFrame
  private var lastPingSent = Game.getFrame
  private val pingFrequency = 5*60
  private val maxPingFrames = 60*60
  private var running = true

  def kill() {
    running = false
  }

  override def execute() {
    while(running) {
      Thread.sleep(350)
      if (Game.getFrame - lastPingSent > pingFrequency) {
        Log.info("Sending ping " + Game.getFrame)
        lastPingSent = Game.getFrame
        client.sendPing()
      }
      if (Game.getFrame - lastPongReceived > maxPingFrames) {
        client.doTimeout()
        Log.info("Ping timeout %d seconds" format (maxPingFrames/60))
        running = false
      }
    }
    Log.info("Pinger shutting down")
  }

  def pong() {
    lastPongReceived = Game.getFrame
  }
}
