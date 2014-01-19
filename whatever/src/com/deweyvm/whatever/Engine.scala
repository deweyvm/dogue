package com.deweyvm.whatever

import com.deweyvm.whatever.graphics.{GlyphFactory, Renderer}
import com.deweyvm.whatever.world.Stage
import com.deweyvm.whatever.input.Controls
import java.net.{InetAddress, Socket}
import com.badlogic.gdx.Gdx
import com.deweyvm.whatever.data.Encoding

class Engine {
  val address = if (Game.globals.IsRemote) "74.243.160.86" else "localhost"
  val sock = new Socket(address, 4815)
  sock.getOutputStream.write(Encoding.toBytes("this is a test\0"))
  val codePage = Assets.page437_16x16
  val cols = Game.RenderWidth/codePage.tileWidth
  val rows = Game.RenderHeight/codePage.tileHeight
  var stage = Stage.createWorld(codePage, cols, rows)

  def update() {
    stage = stage.update
    Controls.update()
    if (Controls.Escape.justPressed) {
      sock.getOutputStream.write(Encoding.toBytes("/quit\0"))
      sock.close()
      Gdx.app.exit()
    }
  }

  def draw() {
    stage.draw()
    Renderer.render()
  }
}
