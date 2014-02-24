package com.deweyvm.dogue

case class DogueOptions(isDebug:Boolean = false,
                        logLevel:String = "",
                        testsOnly:Boolean = false,
                        failFirst:Boolean = true,
                        version:Boolean = false,
                        headless:Boolean = false)
