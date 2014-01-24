package com.deweyvm.dogue.input

import com.deweyvm.gleany.saving.ControlName

class WhateverControl(descriptor: String) extends ControlName {
  override def name: String = descriptor
}
