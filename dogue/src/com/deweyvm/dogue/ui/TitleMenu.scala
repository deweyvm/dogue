package com.deweyvm.dogue.ui

import com.deweyvm.dogue.world.Stage
import com.deweyvm.dogue.common.data.Pointer
import com.deweyvm.dogue.input.Controls
import com.deweyvm.gleany.graphics.Color

object TitleMenu {
  def create(bgColor:Color, f:() => Stage) = {
    val control = () => Controls.Space.justPressed
    val buttons = ButtonFactory.create(control, 10, 5)(
      "World Viewer", f
    )(
      "New Game", f
    )(
      "Exit", () => throw new Exception()
    ).create
    new TitleMenu(f, buttons)
  }
}


case class TitleMenu(f:() => Stage, buttons:Pointer[Button[Stage]]) extends Menu[Stage] {
  def update: Menu[Stage] = {
    val newButtons = buttons.updated(Controls.AxisY.justPressed)

    copy(buttons = newButtons.mapOne {_.update})
  }

  def getResult:Option[Stage] = {
    buttons.get.getResult
  }

  def draw() {
    buttons.foreach(_.drawBg(), _.draw())
  }
}
