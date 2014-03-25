package com.deweyvm.dogue.ui

import com.deweyvm.dogue.graphics.WindowRenderer
import com.deweyvm.dogue.{Dogue, Game}
import com.deweyvm.dogue.common.CommonImplicits._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.parsing.CommandParser
import com.deweyvm.dogue.net.{Transmitter, Client}
import com.badlogic.gdx.InputAdapter
import com.deweyvm.dogue.common.protocol.{LocalInvalid, DogueOps, LocalCommand, DogueMessage}
import com.deweyvm.dogue.common.logging.Log
import com.deweyvm.dogue.entities.Tile
import com.deweyvm.dogue.common.data.Code

trait TextInputId


object ChatInput {
  def create(input:NewTextInput) = ChatInput(input, Seq())
}

case class ChatInput(input:NewTextInput, links:Seq[WindowId]) extends WindowContents {
  def addLink(id:WindowId) = copy(links = links :+ id)
  override def outgoing: Map[WindowId,Seq[WindowMessage]] = {
    input.getOutput.map { s =>
      links.map { l => l -> Vector(WindowMessage.TextMessage(s))}.toMap
    }.getOrElse(Map())

  }

  override def update(s: Seq[WindowMessage]): (Option[ChatInput], Seq[Window]) = {
    (copy(input = input.update).some, Seq())
  }

  override def draw(r:WindowRenderer):WindowRenderer = {
    input.draw(0, 0, 30, 2)(r)
  }
}

object NewTextInput {
  private val entered = scala.collection.mutable.Map[TextInputId, String]()
  private val strings = scala.collection.mutable.Map[TextInputId, String]().withDefaultValue("")
  private val inputs = scala.collection.mutable.Map[TextInputId, NewTextInput]()

  addListener()
  private var active:Option[TextInputId] = None

  def create(name:String, bgColor:Color, fgColor:Color):NewTextInput = {
    val result = new NewTextInput(name, "", bgColor, fgColor, new TextInputId{})
    result
  }

  private def takeOutput(id:TextInputId):Option[String] = {
    if (entered.contains(id)) {
      val result = entered(id)
      entered.remove(id)
      result.some
    } else {
      None
    }
  }

  private def take(id:TextInputId):String = strings(id)

  private def addListener() {
    Dogue.gdxInput foreach {
      _.setInputProcessor(new InputAdapter {
        override def keyTyped(char:Char):Boolean = {
          //todo, make a collective reference where you can just query .string, .queue, etc
          val code = char.toInt
          active foreach { id =>
            if (code == 8) {
              strings(id) = strings(id).dropRight(1)
            } else if (code == 13 && strings(id).length > 0) {
              entered(id) = strings(id)
              println("entered")
              strings(id) = ""
            } else if (code > 31){
              strings(id) += char
            }
          }
          false
        }
      })
    }

  }



}

case class NewTextInput(prompt:String, string:String, bgColor:Color, fgColor:Color, id:TextInputId) {
  NewTextInput.inputs(id) = this

  private def makeText(s:String) = {
    Text.fromString(bgColor, fgColor)(s)
  }


  val cursor = Vector(makeText("_"), makeText(" "))
  val flashRate = 30 //cursor flashes on/off for `flashRate` frames

  def update:NewTextInput = {
    NewTextInput.active = Some(id)
    this.copy(string = NewTextInput.take(id))
  }

  def getOutput = {
    NewTextInput.takeOutput(id)
  }

  def drawCursor(iRoot:Int, jRoot:Int, end:Int, text:Vector[Text])(r:WindowRenderer):WindowRenderer = {

    r <+|? (text.length >= 0 && text(0).width > 0).partial(
      cursor(Game.getFrame/flashRate % 2).draw(iRoot+text(text.length - 1).width, jRoot + end - 1)
    )
  }

  def draw(iRoot:Int, jRoot:Int, width:Int, height:Int)(r:WindowRenderer):WindowRenderer = {
    val text: Vector[Text] = (prompt + string).toLines(width) map makeText
    val iStart = math.max(0, text.length - height)
    val end = math.min(text.length, height)
    val lines = (0 until end) map { i =>
      text(i + iStart).draw(iRoot, jRoot + i) _
    }
    r <++| lines <+| drawCursor(iRoot, jRoot, end, text)
  }
}
