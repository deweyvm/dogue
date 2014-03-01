package com.deweyvm.dogue.ui

import com.deweyvm.dogue.world.Stage
import com.deweyvm.dogue.common.data.Pointer
import com.deweyvm.dogue.input.Controls
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.Implicits
import Implicits._

object TitleMenu {
  def create(bgColor:Color, f:() => Stage) = {
    val control = () => Controls.Space.justPressed
    val buttons = ButtonFactory.create(control, 10, 5, ("World Viewer", f)).add(
      "New Game", f
    ).add(
      "Exit", () => throw new Exception()
    ).create
    new TitleMenu(f, buttons)
  }
}

object ButtonFactory {
  def create[T](activator:() => Boolean, iStart:Int, jStart:Int, first:(String, ()=>T)):ButtonFactory[T] = {
    def makeButton(s:String, f:()=>T, ct:Int) = {
      new Button(new Text(s, Color.Blue, Color.White), f, activator, iStart, jStart + ct)
    }
    val firstButton = makeButton(first._1, first._2, 0)
    ButtonFactory[T](activator, iStart, jStart, 1, firstButton, Vector(), makeButton)
  }
}

case class ButtonFactory[T] private (activator:() => Boolean, iStart:Int, jStart:Int, ct:Int, first:Button[T], rest:Vector[Button[T]], makeButton:(String, ()=>T, Int) => Button[T]) {

  def add(s:String, f:() => T):ButtonFactory[T] = {
    val button = makeButton(s, f, ct)
    val newVec = rest :+ button
    copy(rest = newVec, ct = ct + 1)
  }

  def create:Pointer[Button[T]] = Pointer.create(first, rest:_*)
}

class Button[T](t:Text, result:() => T, shouldActivate:() => Boolean, i:Int, j:Int) extends Menu[T] {
  lazy val lazyResult = result()

  def update = this

  def getResult:Option[T] = {
    shouldActivate().select(lazyResult.some, None)
  }

  def draw() {
    t.draw(i, j)
  }

  def drawBg() {
    t.copy(bgColor = Color.Black).draw(i, j)
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
