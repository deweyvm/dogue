package com.deweyvm.dogue.ui

import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.Transmitter
import com.deweyvm.dogue.common.protocol.{Command, DogueOps, DogueMessage}
import com.deweyvm.dogue.common.parsing.CommandParser
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._
import com.deweyvm.dogue.graphics.WindowRenderer
import com.deweyvm.dogue.ui.WindowMessage.TextMessage

object ChatPanel {
  def create(transmitter:Transmitter[DogueMessage])(makeText:String=>Text) = ChatPanel(transmitter, Vector(), makeText)
}

case class ChatPanel(transmitter:Transmitter[DogueMessage], text:Vector[Text], makeText:String=>Text) extends WindowContents {
 private val parser = new CommandParser
  def update(msgs:Seq[WindowMessage]):(Option[ChatPanel], Seq[Window]) = {
    val (command, say) = msgs.map {
      case TextMessage(t) => t.some
      case _ => None
    }.flatten.partition {
      case s if s.startsWith("/") => true
      case _ => false
    }

    //val commands: Seq[ChatPanel.this.parser.ParseResult[DogueMessage]] = command.map{_.drop(1)}.map{parser.parseCommand(_)}

    val says = say.map(s => transmitter.makeCommand(DogueOps.Say, Vector("dummysigil", s)))
    says foreach transmitter.enqueue
    val received = transmitter.dequeue.map {
      case Command(DogueOps.Say, _, _, args) => ("<%s> %s" format (args(0), args(1))).some
      case Command(DogueOps.Greet, _, _, args) => args(0).some
      case x =>  println(x) ; None
    }.flatten.map(makeText)
    (copy(text = text ++ received).some, Seq())
  }

  def draw(r:WindowRenderer):WindowRenderer = {
    val draws = text.zipWithIndex map { case (t, i) => t.draw(0, i) _ }
    r <++| draws
  }
}

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
