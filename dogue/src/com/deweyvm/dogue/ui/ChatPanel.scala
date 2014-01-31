package com.deweyvm.dogue.ui

import com.deweyvm.dogue.graphics.GlyphFactory
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.Transmitter
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.Game
import com.deweyvm.dogue.common.protocol.DogueMessage
import com.deweyvm.dogue.common.parsing.CommandParser

class TextOutput {
  def update(commands:Vector[String]):TextOutput =  {
    commands foreach { c =>
      Log.info("outputting command " + c)
    }
    this
  }
  def draw(iRoot:Int, jRoot:Int) {


  }
}

case class ChatPanel(override val x:Int,
                     override val y:Int,
                     override val width:Int,
                     override val height:Int,
                     bgColor:Color,
                     fgColor:Color,
                     factory:GlyphFactory,
                     transmitter:Transmitter[DogueMessage],
                     input:TextInput,
                     output:InfoPanel)
  extends Panel(x, y, width, height, bgColor) {
  val inputHeight = 3
  val parser = new CommandParser
  override def getRects:Vector[Recti] =
    Vector(Recti(x, y, width, height - inputHeight - 1),
           Recti(x, y + (height - inputHeight), width, inputHeight))

  override def update = {
    val (newInput, commands) = input.update
    commands foreach transmitter.enqueue
    val newPosted = transmitter.dequeue
    val newOutput = newPosted.foldLeft(output) { case (op:InfoPanel, next:DogueMessage) =>
      op.addText(next.toString, bgColor, fgColor)
    }

    this.copy(input = newInput, output = newOutput.update)
  }

  override def draw() {
    super.draw()
    output.draw()
    input.draw(x, y + height - inputHeight)
  }

}
