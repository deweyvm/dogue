package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.Transmitter
import com.deweyvm.dogue.common.protocol.DogueMessage
import com.deweyvm.dogue.common.parsing.CommandParser


/*case class ChatInput(transmitter:Transmitter[DogueMessage],
                     textColor:Color,
                     input:TextInput,
                     outputId:WindowId) extends WindowContents {
  val inputHeight = input.height
  val parser = new CommandParser
  /*def outgoing:Map[WindowId, Seq[WindowMessage]]
  def update(s:Seq[WindowMessage]):self.type
  def draw():Unit*/
  def outgoing:Map[WindowId, Seq[WindowMessage]] = ???
  override def update(msgs:Seq[WindowMessage]) = {
    /*val (newInput, commands) = input.update(transmitter)
    val (serverCommands, localCommands) = commands.partition {
      case cmd@Command(op, _,_,_) =>
        op match {
          case DogueOps.LocalMessage => false
          case _ => true

        }
    }
    serverCommands foreach transmitter.enqueue

    val newPosted = transmitter.dequeue ++ localCommands*/
    /*val newOutput = newPosted.foldLeft(output) { case (panel:InfoPanel, next:DogueMessage) =>

      next match {
        case cmd@Command(op, src, dst, args) =>
          op match {
            case DogueOps.Say =>
              panel.addText("<%s> %s" format (args(0), args(1)), bgColor, textColor)
            case DogueOps.Greet =>
              panel.addText(args(0), bgColor, Color.Pink)
            case DogueOps.LocalMessage =>
              Log.info("local message")
              panel.addText(args(0), bgColor, Color.Pink)
            case DogueOps.Reassign =>
              Client.setName(dst)
              panel
            case DogueOps.Assign =>
              Client.setName(args(0))
              Game.settings.password.set(args(1))
              Game.settings.username.set(args(0))
              panel
            case _ =>
              Log.warn("Don't know how to process command \"%s\"" format cmd)
              panel
          }
        //
        case _ => panel
      }
    }*/
    //this.copy(output = newOutput.update)
    this
  }

  def draw() {
    //output.drawBackground()
    //input.draw(x, y + height - inputHeight)
  }

}*/
