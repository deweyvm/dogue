package com.deweyvm.dogue.input

import com.deweyvm.gleany.saving.ControlName

class DogueControl(descriptor: String) extends ControlName {
  override def name: String = descriptor
}
