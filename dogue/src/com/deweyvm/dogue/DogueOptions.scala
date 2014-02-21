package com.deweyvm.dogue

case class DogueOptions(isDebug:Boolean = false,
                        testsOnly:Boolean = false,
                        failFirst:Boolean = false,
                        version:Boolean = false,
                        headless:Boolean = false)
