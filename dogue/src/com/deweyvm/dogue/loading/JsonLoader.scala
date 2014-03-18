package com.deweyvm.dogue.loading

import com.badlogic.gdx.utils.{SerializationException, Json}
import com.deweyvm.dogue.common.data.{Writer, Encoding}
import java.nio.file.{Files, Paths}
import scala.reflect.ClassTag
import java.io.{IOException, UnsupportedEncodingException}

object JsonLoader {
  def fromFile[T : Manifest](filename:String):LoadResult[T] = {
    def fail(msg:String, e:Exception): Writer[Vector[String], T] =
      Writer.error(Vector(msg, e.toString)).fromFile(Vector(filename))
    try {
      val bytes = Files.readAllBytes(Paths.get(filename))
      val string = Encoding.fromBytes(bytes, bytes.length)
      val json = new Json()
      val c: Class[T] = manifest[T].runtimeClass.asInstanceOf[Class[T]]
      Writer.unit[Vector[String],T](json.fromJson[T](c, string)).fromFile(Vector(filename))
    } catch {
      case ioe:IOException =>
        fail("Failed to load file", ioe)
        throw ioe
      case oee:UnsupportedEncodingException =>
        fail("Couldn't encode file", oee)
        throw oee
      case ser:SerializationException =>
        fail("Json error", ser)
        throw ser
    }
  }
}
