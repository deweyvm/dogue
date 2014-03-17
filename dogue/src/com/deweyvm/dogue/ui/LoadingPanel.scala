package com.deweyvm.dogue.ui

import com.deweyvm.gleany.data.Recti
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.threading.ProgressFuture

class LoadingPanel(rect:Recti, bgColor:Color, panel:ProgressFuture[WorldPanel]) extends Panel(rect, bgColor) {
  override def update = {
    panel.getResult.getOrElse(this)
  }

  override def draw() {
    super.draw()
    Text.create(bgColor, Color.White).append("Progress " + (panel.getProgress * 100).toInt).draw(10,10)
  }
}
