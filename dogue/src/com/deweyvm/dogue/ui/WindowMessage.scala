package com.deweyvm.dogue.ui

trait WindowMessage

case class TextMessage(s:String) extends WindowMessage
case object Clear extends WindowMessage
