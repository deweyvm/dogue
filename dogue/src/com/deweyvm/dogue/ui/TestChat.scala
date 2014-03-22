package com.deweyvm.dogue.ui

import com.deweyvm.dogue.graphics.WindowRenderer

class TestChat(input:TextInput) extends WindowContents {

  def outgoing: Map[WindowId,Seq[WindowMessage]] = ???
  def spawnWindow: Option[Window] = ???
  def update(s: Seq[WindowMessage]): Option[com.deweyvm.dogue.ui.WindowContents] = ???

  def draw(r:WindowRenderer):WindowRenderer = {
    input.draw(0, 0)(r)
  }
}
