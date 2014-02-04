package com.deweyvm.dogue

import util.Random
import com.deweyvm.gleany.Debug
import com.deweyvm.dogue.common.Implicits._

class Globals {
  Debug.load()
  private var drawDebug = false
  var IsDebugMode = true

  /*def setAddress(address:String) {
    remoteIp = address.some
  }

  def setPort(p:Int) {
    port = p.some
  }

  private var remoteIp:Option[String] = None
  private var port:Option[Int] = None


  def getPort = port.getOrElse(4815)
  def getAddress = remoteIp.getOrElse("localhost")*/

  var Version = "0.X.X"

  def shouldDrawDebug: Boolean = drawDebug

  def toggleDrawDebug() {
    drawDebug = !drawDebug
  }
}
