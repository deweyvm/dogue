package com.deweyvm.dogue

case class DogueOptions(log:String = ".",
                        isDebug:Boolean = false,
                        version:Boolean = false,
                        address:String = "localhost",
                        port:Int = 0)
