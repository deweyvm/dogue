package com.deweyvm.dogue.ui

import com.deweyvm.dogue.graphics.WindowRenderer
import com.deweyvm.gleany.data.Recti
import com.deweyvm.gleany.graphics.Color

trait WindowContents {
  self =>
  def outgoing:Map[WindowId, Seq[WindowMessage]]
  def update(s:Seq[WindowMessage]):Option[WindowContents]
  def spawnWindow:Option[Window]
  def draw(r:WindowRenderer):WindowRenderer

  /**
   *
   * @param rect the rect of the contents. the rect of the window will be larger by 2x2
   * @param bgColor the background color of the window
   * @return The created window and its associated ID
   */
  def makeWindow(rect:Recti, bgColor:Color):Window = {
    val id = new WindowId{}
    Window(rect.expand(1, 1), bgColor, this, id)
  }
}
