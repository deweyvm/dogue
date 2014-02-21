package com.deweyvm.dogue.graphics.visualizers

import com.deweyvm.dogue.Game
import com.deweyvm.dogue.common.procgen.VectorField
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.graphics.OglRenderer

class VectorFieldVisualizer extends Visualizer {
  var seed = 0L
  var vectorField = VectorField.perlinWindOrig(Game.RenderWidth, Game.RenderHeight, 20, seed)


  override def drawShape(r:OglRenderer) {
    val shape = r.shape
    val camera = r.camera

    camera.zoom(2)
    r.translateShape(Game.RenderWidth/2,Game.RenderHeight/2) {() =>
      vectorField.vectors foreach {case (pt, arrow, lineColor) =>
        val (line, (l1, l2, l3)) = arrow.getShapes(pt)
        shape.begin(ShapeType.Line)
        shape.setColor(lineColor.toLibgdxColor)

        shape.line(line.p.x.toFloat, line.p.y.toFloat, line.q.x.toFloat, line.q.y.toFloat)
        shape.end()
        shape.begin(ShapeType.Filled)
        shape.triangle(
          l1.p.x.toFloat,
          l1.p.y.toFloat,
          l2.q.x.toFloat,
          l2.q.y.toFloat,
          l3.p.x.toFloat,
          l3.p.y.toFloat
        )
        shape.end()
      }

      if (Controls.Space.justPressed) {
        seed += 1
        vectorField = VectorField.perlinWindOrig(Game.RenderWidth, Game.RenderHeight, 20, seed)
      }
    }
    camera.zoom(1)
  }
}
