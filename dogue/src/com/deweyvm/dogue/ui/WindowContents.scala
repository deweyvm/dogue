package com.deweyvm.dogue.ui

import com.deweyvm.dogue.graphics.{ColorScheme, WindowRenderer}
import com.deweyvm.gleany.data.Recti
import com.deweyvm.gleany.graphics.Color

trait WindowContents {
  self =>
  def outgoing:Map[WindowId, Seq[WindowMessage]] = Map()
  def update(s:Seq[WindowMessage]):(Option[WindowContents], Seq[Window])
  def draw(r:WindowRenderer):WindowRenderer

  /**
   * Creates a window from this
   * @param rect the rect of the contents. the rect of the window will be larger by 2x2
   * @param scheme the colorscheme to use for coloring text
   * @return The created window and its associated ID
   */
  def makeWindow(rect:Recti, scheme:ColorScheme):Window = {
    val id = new WindowId{}
    Window(rect.expand(1, 1), scheme, this, id)
  }
}
