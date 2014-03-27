package com.deweyvm.dogue.net

import com.deweyvm.dogue.common.protocol.{DogueOp, Command}


trait Transmitter[T] {
  def enqueue(s:T):Unit
  def dequeue:Vector[T]

  def sourceName:String
  def destinationName:String

  def makeCommand(op:DogueOp, args:Vector[String]):Command =
    Command(op, sourceName, destinationName, args)
}
