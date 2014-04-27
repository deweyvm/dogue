package com.deweyvm.dogue.ui

import com.deweyvm.dogue.common.data.Pointer
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.graphics.WindowRenderer

object TitleMenu {
  def create(toWorld:() => Seq[Window], toDungeon:() => Seq[Window]) = {
    val control = () => Controls.Space.justPressed
    val buttons = ButtonFactory.create(control, 10, 5)(
      "World Viewer", toWorld
    )(
      "Dungeon Viewer", toDungeon
    )(
      "Exit", () => throw new Exception()
    ).create
    new TitleMenu(buttons)
  }
}


case class TitleMenu(buttons:Pointer[Button[Seq[Window]]]) extends Menu[Seq[Window]] {
  def update: Menu[Seq[Window]] = {
    val newButtons = buttons.updated(Controls.AxisY.justPressed)
    copy(buttons = newButtons.getMap {_.update})
  }

  def getResult:Option[Seq[Window]] = {
    buttons.get.getResult
  }

  def draw(r:WindowRenderer):WindowRenderer = {
    r <++| buttons.selectMap(_.drawBg _, _.draw _)
  }
}
