package com.deweyvm.dogue.ui

import com.deweyvm.gleany.data.Recti
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.threading.DogueFuture
import com.deweyvm.dogue.world.Stage
import com.deweyvm.dogue.common.{logging, CommonImplicits}
import CommonImplicits._
import com.deweyvm.dogue.common.data.control.{YieldResult, Return, Yield, Coroutine}
import com.deweyvm.dogue.input.Controls

object LoadingPanel {
  def create(rect:Recti,
             bgColor:Color,
             panel:DogueFuture[Coroutine[WorldPanel]],
             makeStage:Panel => Stage):LoadingPanel = {
    LoadingPanel(0, 0, Vector(), rect, bgColor, panel, makeStage, None, false, false)
  }
}

case class LoadingPanel(progress:Int,
                        totalTime:Long,
                        strings:Vector[String],
                        override val rect:Recti,
                        bgColor:Color,
                        panel:DogueFuture[Coroutine[WorldPanel]],
                        makeStage:Panel => Stage,
                        newStage:Option[Stage],
                        failed:Boolean,
                        finished:Boolean) extends Panel(rect, bgColor) {

  override def update:Panel = {
    panel.getFailure match {
      case Some(exc) if !failed =>
        val error = logging.Log.formatStackTrace(exc)
        return this.copy(failed=true, strings=error.split(System.lineSeparator()).toVector ++ strings)

      case None => ()
      case _ => return this
    }
    panel.getResult match {
      case Some(c@Return(f)) =>
        if (Controls.Insert.justPressed) {
          copy(newStage = makeStage(f()).some)
        } else if (!finished) {
          prepend("Finished").appendCurrent("Total: " + totalTime/1000000 + " ms").copy(finished = true)
        } else {
          this
        }
      case Some(c@YieldResult(nanos, f)) =>
        val future = DogueFuture.createAndRun(() => f)
        val timeString = "Done in %dms" format (nanos/1000000)
        copy(panel = future, totalTime = totalTime + nanos).appendCurrent(timeString)
      case Some(c@Yield(p, l, f)) =>
        copy(progress=p, panel = DogueFuture.createAndRun(f)).prepend(l)
      case _ => this
    }


  }

  private def appendCurrent(s:String) = {
    val padding = 28
    val newStrings = strings match {
      case current +: rest =>
        val curLen = math.max(padding - current.length, 0)
        val space = " "*curLen
        ("%s...%s%s" format (current,space, s)) +: rest
      case otherwise => otherwise
    }
    copy(strings = newStrings)
  }

  private def prepend(s:String) = copy(strings = s +: strings)



  override def requestStage:Option[Stage] = newStage

  override def draw() {
    super.draw()
    Text.create(bgColor, Color.White).append("Progress " + progress).draw(10,10)
    strings.zipWithIndex.foreach{case (s, i) =>
      Text.create(bgColor, Color.White).append(s).draw(10, 11 + i)
    }

  }
}
