package com.deweyvm.dogue.graphics

import com.badlogic.gdx.graphics.Texture
import com.deweyvm.dogue.common.data.Array2d
import com.deweyvm.gleany.AssetLoader
import com.deweyvm.gleany.data.Recti
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.deweyvm.dogue.common.CommonImplicits
import CommonImplicits._

case class Tileset(cols:Int,
                   rows:Int,
                   tileWidth:Int,
                   tileHeight:Int,
                   texture:Texture) {
  private val regions = Array2d.tabulate(cols, rows) { case (i, j) =>
    AssetLoader.makeTextureRegion(texture,Recti(i * tileWidth, j * tileHeight, tileWidth, tileHeight).some)
  }

  def getRegion(i:Int, j:Int):TextureRegion = {
    regions.get(i, j)
  }
}
