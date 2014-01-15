package com.deweyvm.whatever.input

import com.deweyvm.gleany.input.triggers.{KeyboardTrigger, TriggerAggregate}
import com.badlogic.gdx.Input
import com.deweyvm.gleany.input.Control



object Controls {
  val Up = makeControl(Input.Keys.UP)
  val Down = makeControl(Input.Keys.DOWN)
  val Right = makeControl(Input.Keys.RIGHT)
  val Left = makeControl(Input.Keys.LEFT)
  val All = Vector(Up, Down, Right, Left)

  def makeControl(key:Int) = {
    new TriggerAggregate(Seq(new KeyboardTrigger(key)))
  }

  def update() {
    All foreach {_.update()}
  }


}
