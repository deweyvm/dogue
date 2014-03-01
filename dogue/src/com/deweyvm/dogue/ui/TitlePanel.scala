package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.world.{Stage, StageFactory}


object TitlePanel {
  def create(rect:Recti, factory:StageFactory, bgColor:Color) = {
    TitlePanel(rect, factory, bgColor, TitleMenu.create(bgColor, () => factory.create(Stage.World)))
  }
}

case class TitlePanel(override val rect:Recti,
                      factory:StageFactory,
                      bgColor:Color,
                      menu:Menu[Stage])
  extends Panel(rect, bgColor) {
  val title = new Text("Dogue", Color.Black, Color.White)
  override def requestStage:Option[Stage] = {
    menu.getResult
  }

  override def update:Panel = {
    copy(menu = menu.update)
  }

  override def draw() {
    super.draw()
    title.draw(width/2 - title.width/2, height/2)
    menu.draw()
  }

}
