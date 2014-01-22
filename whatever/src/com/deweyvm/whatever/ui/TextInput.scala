package com.deweyvm.whatever.ui

import com.badlogic.gdx.{InputAdapter, Gdx}
import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.gleany.graphics.Color
import scala.collection.mutable.ArrayBuffer
import com.deweyvm.whatever.Game

object TextInput {
  var count = 0

  def create(width:Int, height:Int, bgColor:Color, fgColor:Color, factory:GlyphFactory):TextInput = {
    val result = new TextInput(count, width, height, bgColor, fgColor, "", factory)
    count += 1
    result
  }

  def addListener() {
    Gdx.input.setInputProcessor(new InputAdapter {
      override def keyTyped(char:Char):Boolean = {
        //todo, make a collective reference where you can just query .string, .queue, etc
        val code = char.toInt
        active foreach { id =>
          if (code == 8) {
            strings(id) = strings(id).dropRight(1)
          } else if (code == 13) {
            commandQueue(id) += strings(id)
            strings(id) = ""
          } else if (code > 31){
            strings(id) += char
            println("here <%s>".format(char.toInt.toString))
          }
        }
        false
      }
    })
  }

  def take(id:Int):String = {
    val added = strings(id)
    added
  }

  val strings = scala.collection.mutable.Map[Int, String]().withDefaultValue("")
  val inputs = scala.collection.mutable.Map[Int, TextInput]()
  val commandQueue = scala.collection.mutable.Map[Int, ArrayBuffer[String]]().withDefaultValue(ArrayBuffer[String]())
  var active:Option[Int] = None
}

case class TextInput(id:Int, width:Int, height:Int, bgColor:Color, fgColor:Color, string:String, factory:GlyphFactory) {
  TextInput.inputs(id) = this

  private def makeText(s:String) = {
    new Text(s, bgColor, fgColor, factory)
  }

  //todo -- code clones with info panel: put this in a more sensible place
  val text = InfoPanel.splitText(string, width) map makeText
  val cursor = Vector(makeText("_"), makeText(" "))
  val flashRate = 30 //flash cursor alternatively for flashRate frames

  def update:TextInput = {
    TextInput.active = Some(id)
    this.copy(string = TextInput.take(id))
  }

  def draw(iRoot:Int, jRoot:Int) {
    val iStart = scala.math.max(0, text.length - height)
    val end = scala.math.min(text.length, height)
    for (i <- 0 until end) {
      text(i + iStart).draw(iRoot, jRoot + i)
    }
    if (text.length >= 0 && text(0).width > 0)  {
      cursor(Game.getFrame/flashRate % 2).draw(iRoot+text(text.length - 1).width, jRoot + end - 1)
    }
  }
}
