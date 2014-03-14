package com.deweyvm.dogue.loading

import com.badlogic.gdx.utils.Json
import com.deweyvm.dogue.common.data.Encoding
import java.nio.file.{Files, Paths}
import scala.reflect.ClassTag

object Loader {
  def fromFile[T : Manifest](filename:String):T = {
    val bytes = Files.readAllBytes(Paths.get(filename))
    val string = Encoding.fromBytes(bytes, bytes.length)
    val json = new Json()
    val c: Class[T] = manifest[T].runtimeClass.asInstanceOf[Class[T]]
    json.fromJson[T](c, string)
  }
}
