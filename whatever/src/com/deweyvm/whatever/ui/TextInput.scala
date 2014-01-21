package com.deweyvm.whatever.ui

import com.badlogic.gdx.{InputAdapter, Gdx}
import com.deweyvm.whatever.graphics.GlyphFactory
import com.deweyvm.gleany.graphics.Color
import scala.collection.mutable.ArrayBuffer

object TextInput {
  var count = 0
  def create(bgColor:Color, fgColor:Color, factory:GlyphFactory):TextInput = {
    val result = new TextInput(count, Text.create(bgColor, fgColor, factory))
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
          } else {
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

case class TextInput(id:Int, text:Text) {
  TextInput.inputs(id) = this
  def update:TextInput = {
    TextInput.active = Some(id)
    this.copy(text = text.replace(TextInput.take(id)))
  }

  def draw(iRoot:Int, jRoot:Int) {
    text.draw(iRoot, jRoot)
  }
}
