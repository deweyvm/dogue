package com.deweyvm.dogue.ui

import com.deweyvm.gleany.data.Recti
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.threading.DogueFuture
import com.deweyvm.dogue.world.Stage
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.ui.WorldPanel.{Yield, Return, Coroutine}

case class LoadingPanel(progress:Int, label:String, override val rect:Recti, bgColor:Color, panel:DogueFuture[Coroutine[WorldPanel]], makeStage:Panel => Stage, newStage:Option[Stage]=None) extends Panel(rect, bgColor) {
  override def update = {
    panel.getResult match {
      case Some(Return(p, l, f)) => copy(progress=p, label=l, newStage = makeStage(f()).some)
      case Some(Yield(p, l, f)) => copy(progress=p, label=l, panel = DogueFuture.createAndRun(f))
      case _ => this
    }
  }

  override def requestStage:Option[Stage] = newStage

  override def draw() {
    super.draw()
    Text.create(bgColor, Color.White).append("Progress " + progress).draw(10,10)
    Text.create(bgColor, Color.White).append(label).draw(10, 11)
  }
}
