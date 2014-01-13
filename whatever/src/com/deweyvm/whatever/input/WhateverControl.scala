package com.deweyvm.whatever.input

import com.deweyvm.gleany.saving.ControlName

class WhateverControl(descriptor: String) extends ControlName {
  override def name: String = descriptor
}
