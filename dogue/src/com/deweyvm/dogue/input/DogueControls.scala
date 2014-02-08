package com.deweyvm.dogue.input

import com.deweyvm.gleany.saving.ControlNameCollection

object DogueControls extends ControlNameCollection[DogueControl] {
  def fromString(string: String): Option[DogueControl] = None
  def makeJoypadDefault: Map[String,String] = Map()
  def makeKeyboardDefault: Map[String,java.lang.Float] = Map()
  def values: Seq[DogueControl] = Seq()
}
