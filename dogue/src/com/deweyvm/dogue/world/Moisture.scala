package com.deweyvm.dogue.world

import com.deweyvm.dogue.common.data.{Lazy2d, Indexed2d}
import com.deweyvm.dogue.common.Implicits.Meters
import com.deweyvm.gleany.data.Point2d
import com.deweyvm.dogue.common.procgen.Arrow
import com.deweyvm.gleany.graphics.Color

class Moisture(height:Indexed2d[Meters], wind:Lazy2d[Arrow]) {

}

class Particle(i:Int, j:Int, maxHeight:Meters)
