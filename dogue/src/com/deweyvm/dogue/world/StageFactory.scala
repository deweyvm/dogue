package com.deweyvm.dogue.world

import com.deweyvm.dogue.ui._
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.net.Client
import com.deweyvm.gleany.data.Recti
import com.deweyvm.dogue.common.procgen.{PerlinNoise, PerlinParams}
import com.deweyvm.dogue.world.Stage.Chat
import com.deweyvm.dogue.common.threading.DogueFuture
import com.deweyvm.dogue.loading.Loads
import com.deweyvm.dogue.loading

class StageFactory(cols:Int, rows:Int) {
  val serverText = Text.create(Color.Black, Color.White)
  val bgColor = Color.Blue
  private def makeStage(panels:Panel*) = {
    Stage(cols, rows, panels.toVector, serverText)
  }

  def create(t:StageType):Stage = {
    t match {
      case Stage.Blank =>
        val blankRect = Recti(1, 1, cols - 2, rows - 2)
        makeStage(new Panel(blankRect, Color.Black))
      case Stage.Title =>
        val titleRect = Recti(1, 1, cols - 2, rows - 2)
        val titlePanel = TitlePanel.create(titleRect, this, bgColor)
        makeStage(titlePanel)
      case Stage.Chat =>
        val inputHeight = 3
        val bgColor = Color.Black
        val fgColor = Color.White
        val textInput = TextInput.create(TextInput.chat, cols - 2, inputHeight, bgColor, fgColor)
        val infoRect = Recti(1, 1, cols - 2, rows - 2 - inputHeight - 3)
        val textOutput = InfoPanel.makeNew(infoRect, bgColor)//new TextOutput()
        val chatRect = Recti(1, 1, cols - 2, rows - 2)
        val chatPanel = new ChatPanel(chatRect, bgColor, fgColor, Client.instance, textInput, textOutput)
        makeStage(chatPanel)
      case Stage.World =>
        val worldSize = 256
        val controlsHeight = 8
        val minSideWidth = 24
        val minimapSize = 69
        val maxMinimapPanelSize = minimapSize + 3
        val (sideWidth, mapWidth) =
          if (cols > minSideWidth + maxMinimapPanelSize) {
            (cols - maxMinimapPanelSize, maxMinimapPanelSize)
          } else {
            (minSideWidth, cols - minSideWidth)
          }
        val mapRect = Recti(sideWidth + 2, 1, mapWidth - 3, rows - 2)
        var progress = 0.0
        var description = ""
        val future = DogueFuture.runProgress(() => {
          val seed = 0//System.nanoTime
          println("Seed: " + seed + "L")
          val date = DateConstants(framesPerDay = 60*60*24*60)
          val perlin = PerlinParams(worldSize/4, 8, worldSize, seed)
          val params = WorldParams(minimapSize, perlin, date)
          val (s1, next1) = Loader.getLoaders(params)
          progress = 0.125
          description = s1
          val (s2, next2) = next1()
          progress += 0.25
          description = s2
          val (s3, next3) = next2()
          progress += 0.375
          description = s3
          val (s4, next4) = next3()
          progress += 0.5
          description = s4
          val (s5, next5) = next4()
          progress += 0.625
          description = s5
          val (s6, next6) = next5()
          progress += 0.75
          description = s6
          val (s7, next7) = next6()
          progress += 0.875
          description = s7
          val (s8, next8) = next7()
          progress += 0.99
          description = s8
          //val eco = Ecosphere.create(params)
          val world = World.create(params, next8())

          WorldPanel.create(mapRect, sideWidth, rows - controlsHeight - 1, minimapSize, bgColor, worldSize, world, params)
        }, () => progress, () => description)
        val progressPanel = new LoadingPanel(Recti(1, 1, cols - 2, rows - 2), bgColor, future)
        val controlRect = Recti(1, rows - controlsHeight + 1, sideWidth, controlsHeight - 1 - 1)
        val controlPanel = new Panel(controlRect, bgColor)
        makeStage(controlPanel, progressPanel)

    }
  }
}

object Loader {

  def getLoaders(worldParams:WorldParams) = {
    val seed = worldParams.seed
    val cols = worldParams.size
    val rows = worldParams.size
    import loading._
    val loadRegions = () => {
      val (altRegions, latRegions, surfaceRegions) = Loads.loadRegionMaps.get
      val latMap = new LatitudeMap(cols, rows, latRegions)

      val loadBiomes = () => {
        val biomes = Loads.loadBiomes(latRegions, altRegions, surfaceRegions).get
        val loadNoise = () => {
          val noise = new PerlinNoise(worldParams.perlin).render
          val loadSurface = () => {
            val surface = new SurfaceMap(noise, worldParams.perlin, surfaceRegions)
            val loadWindMap = () => {
              val windMap = new StaticWindMap(surface.heightMap, 10000, 1, seed)
              val loadMoisture = () => {
                val moistureMap = new MoistureMap(surface, latMap.latitude, windMap.arrows, 0.5, cols/2, seed)
                val loadBiomeMap = () => {
                  val biomeMap = new BiomeMap(moistureMap, surface, latMap, altRegions, biomes)
                  val loadEcosphere = () => {
                    Ecosphere.buildEcosphere(worldParams, latMap, noise, surface, windMap, moistureMap, biomeMap, surfaceRegions, latRegions, altRegions, Vector())
                  }
                  ("Constructing ecosphere", loadEcosphere)
                }
                ("Choosing biomes", loadBiomeMap)
              }
              ("Plotting average rainfall", loadMoisture)
            }
            ("Generating wind currents", loadWindMap)
          }
          ("Generating surface features", loadSurface)
        }
        ("Generating noise", loadNoise)
      }
      ("Loading biome data", loadBiomes)
    }
    ("Loading region data", loadRegions)
  }
}
