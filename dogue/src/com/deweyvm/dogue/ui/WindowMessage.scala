package com.deweyvm.dogue.ui

import com.deweyvm.dogue.common

trait WindowMessage
object WindowMessage {
  case class TextMessage(s:String) extends WindowMessage
  case class DogueMessage(m:common.protocol.DogueMessage) extends WindowMessage
  case object Clear extends WindowMessage
}

