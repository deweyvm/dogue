package com.deweyvm.whatever.input

import com.deweyvm.gleany.saving.ControlNameCollection

object WhateverControls extends ControlNameCollection[WhateverControl] {
  def fromString(string: String): Option[WhateverControl] = None
  def makeJoypadDefault: Map[String,String] = Map()
  def makeKeyboardDefault: Map[String,java.lang.Float] = Map()
  def values: Seq[WhateverControl] = Seq()
}
