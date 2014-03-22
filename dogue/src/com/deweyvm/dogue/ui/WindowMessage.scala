package com.deweyvm.dogue.ui

trait WindowMessage

case class TextMessage(s:String) extends WindowMessage
