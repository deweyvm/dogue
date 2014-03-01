package com.deweyvm.dogue.ui

import com.deweyvm.dogue.common.data.Pointer
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.ui


object ButtonFactory {
  def create[T](activator:() => Boolean, iStart:Int, jStart:Int)(first:(String, ()=>T)):ButtonFactory[T] = {
    def makeButton(s:String, f:()=>T, ct:Int) = {
      new Button(Text.fromString(s, Color.Blue, Color.White), f, activator, iStart, jStart + ct)
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


