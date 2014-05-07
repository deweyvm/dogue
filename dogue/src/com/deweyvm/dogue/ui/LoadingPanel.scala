package com.deweyvm.dogue.ui

import com.deweyvm.gleany.data.Recti
import com.deweyvm.gleany.graphics.Color
import com.deweyvm.dogue.common.threading.DogueFuture
import com.deweyvm.dogue.world.Workspace
import com.deweyvm.dogue.common.{logging, CommonImplicits}
import CommonImplicits._
import com.deweyvm.dogue.common.data.control.{YieldResult, Return, Yield, Coroutine}
import com.deweyvm.dogue.input.Controls
import com.deweyvm.dogue.ui.world.WorldPanel
import com.deweyvm.dogue.graphics.{ColorScheme, WindowRenderer}

object LoadingPanel {
  def create(rect:Recti,
             scheme:ColorScheme,
             makeWindow:WindowContents => Seq[Window],
             panel:DogueFuture[Coroutine[WorldPanel]]):LoadingPanel = {
    LoadingPanel(0, 0L, Vector(), scheme, panel, makeWindow, failed=false, finished=false)
  }
}

case class LoadingPanel(progress:Int,
                        totalTime:Long,
                        strings:Vector[String],
                        scheme:ColorScheme,
                        panel:DogueFuture[Coroutine[WorldPanel]],
                        makeWindow:WindowContents => Seq[Window],
                        failed:Boolean,
                        finished:Boolean) extends WindowContents {

  def updateFailure() = {
    panel.getFailure match {
      case Some(exc) if !failed =>
        val error = logging.Log.formatStackTrace(exc)
        copy(failed=true, strings=error.split(System.lineSeparator()).toVector ++ strings).some

      case None => None
      case _ => this.some
    }
  }

  override def update(s:Seq[WindowMessage]):(Option[WindowContents], Seq[Window]) = {
    val fail = updateFailure()
    if (fail.isDefined) {
      (fail, Seq())
    } else {
      panel.getResult match {
        case Some(c@Return(f)) =>
          if (Controls.Insert.justPressed) {
            (None, makeWindow(f()))
          } else if (!finished) {
            val self = prepend("Finished").appendCurrent("Total: " + totalTime/1000000 + " ms").copy(finished = true)
            (self.some, Seq())
          } else {
            (this.some, Seq())
          }
        case Some(c@YieldResult(nanos, f)) =>
          val future = DogueFuture.createAndRun(() => f)
          val timeString = "Done in %dms" format (nanos/1000000)
          val self = copy(panel = future, totalTime = totalTime + nanos).appendCurrent(timeString)
          (self.some, Seq())
        case Some(c@Yield(p, l, f)) =>
          val self = copy(progress=p, panel = DogueFuture.createAndRun(f)).prepend(l)
          (self.some, Seq())
        case _ =>
          (this.some, Seq())
      }
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

  override def draw(r:WindowRenderer):WindowRenderer = {
    r <+|
      scheme.makeText("Progress " + progress).draw(10,10) <++|
      strings.zipWithIndex.map{case (s, i) =>
        scheme.makeText(s).draw(10, 11 + i) _
      }

  }
}

