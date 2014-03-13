package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.Array2d
import com.deweyvm.dogue.common.{CommonImplicits, data}
import CommonImplicits._
class LatitudeMap(val cols:Int, val rows:Int) {
  val latitude:data.Array2d[Double] = {
    val max = cols/2
    Array2d.tabulate(cols, rows){ case (i, j) =>
      val x = (cols/2 - i).toDouble
      val y = (rows/2 - j).toDouble
      (x*x + y*y).sqrt/max
    }
  }

  val regions:Array2d[LatitudinalRegion] = latitude.map{ case (i, j, l) =>
    Latitude.getLatitude(l)
  }
}
