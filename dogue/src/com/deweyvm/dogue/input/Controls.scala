package com.deweyvm.dogue.input

import com.deweyvm.gleany.input.triggers.{KeyboardTrigger, TriggerAggregate}
import com.badlogic.gdx.Input
import com.deweyvm.gleany.input.AxisControl
import com.deweyvm.dogue.Game


object Controls {
  val Up = makeControl(Input.Keys.UP)
  val Down = makeControl(Input.Keys.DOWN)
  val Right = makeControl(Input.Keys.RIGHT)
  val Left = makeControl(Input.Keys.LEFT)
  val Enter = makeControl(Input.Keys.ENTER)
  val Space = makeControl(Input.Keys.SPACE)
  val Backspace = makeControl(Input.Keys.BACKSPACE)
  val Tab = makeControl(Input.Keys.TAB)
  val Escape = makeControl(Input.Keys.ESCAPE)
  val Shift = makeControl(Input.Keys.SHIFT_LEFT)

  val AxisX = new AxisControl(Left, Right)
  val AxisY = new AxisControl(Up, Down)

  val All = Vector(Up, Down, Right, Left, Enter, Backspace, Space, Tab, Escape, Shift)

  def makeControl(key:Int) = {
    if (Game.globals.IsHeadless) {
      new NullControl
    } else {
      new TriggerAggregate(Seq(new KeyboardTrigger(key)))
    }
  }

  def update() {
    All foreach {_.update()}
  }
}
