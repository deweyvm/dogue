package com.deweyvm.dogue.ui

import com.deweyvm.dogue.graphics.GlyphFactory
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.{Client, Transmitter}
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.Game
import com.deweyvm.dogue.common.protocol.{DogueOps, Command, DogueMessage}
import com.deweyvm.dogue.common.parsing.CommandParser

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
    val (newInput, commands) = input.update(transmitter)
    commands foreach transmitter.enqueue
    val newPosted = transmitter.dequeue
    val newOutput = newPosted.foldLeft(output) { case (panel:InfoPanel, next:DogueMessage) =>

      next match {
        case cmd@Command(op, src, dst, args) =>
          op match {
            case DogueOps.Say =>
              panel.addText("%s: %s" format (src, args(0)), bgColor, fgColor)
            case DogueOps.Greet =>
              panel.addText(args(0), bgColor, Color.Pink)
            case DogueOps.Reassign =>
              Client.setName(dst)
              panel
            case DogueOps.Assign =>
              Client.setName(args(0))
              Game.settings.password = args(1)
              Game.settings.username = args(0)
              Game.settings.flush()
              panel
            case _ =>
              Log.warn("Don't know how to process command \"%s\"" format cmd)
              panel
          }
          //
        case _ => panel
      }
    }
    this.copy(input = newInput, output = newOutput.update)
  }

  override def draw() {
    super.draw()
    output.draw()
    input.draw(x, y + height - inputHeight)
  }

}
