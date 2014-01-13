package com.explatcreations.whatever.input

import com.explatcreations.gleany.saving.ControlNameCollection

object WhateverControls extends ControlNameCollection[WhateverControl] {
  def fromString(string: String): Option[WhateverControl] = None
  def makeJoypadDefault: Map[String,String] = Map()
  def makeKeyboardDefault: Map[String,java.lang.Float] = Map()
  def values: Seq[WhateverControl] = Seq()
}
