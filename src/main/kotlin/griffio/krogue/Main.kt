package griffio.krogue

import com.varabyte.kotter.foundation.timer.addTimer
import com.varabyte.kotter.foundation.firstSuccess
import com.varabyte.kotter.foundation.input.Keys
import com.varabyte.kotter.foundation.input.onKeyPressed
import com.varabyte.kotter.foundation.input.runUntilKeyPressed
import com.varabyte.kotter.foundation.liveVarOf
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.*
import com.varabyte.kotter.runtime.render.RenderScope
import com.varabyte.kotter.terminal.system.SystemTerminal
import com.varabyte.kotter.terminal.virtual.TerminalSize
import com.varabyte.kotter.terminal.virtual.VirtualTerminal
import com.varabyte.kotterx.decorations.BorderCharacters
import com.varabyte.kotterx.decorations.bordered
import com.varabyte.kotterx.text.shiftRight
import griffio.krogue.tiles.tiles01
import java.time.Duration

val world: List<MutableList<Tile>> = tiles01.map { line ->
    line.map {
        when (it) {
            '#' -> Cave()
            '.' -> Floor()
            '^' -> Lava()
            '~' -> Water()
            else -> error("Unknown tile: $it")
        }
    }.toMutableList()
}

data class View(val min: Int, val max: Int)

infix fun Int.xy(that: Int): View = View(this, that)

fun incView(current: View, max: Int) = if (current.max < max) current.min + 1 xy current.max + 1 else current

fun decView(current: View, min: Int) = if (current.min > min) current.min - 1 xy current.max - 1 else current

// https://en.wikipedia.org/wiki/ANSI_escape_code (Standard colors/High-intensity colors)
sealed class Tile(val glyph: Char, val colorIndex: Int, var isVisible: Boolean, var isOpaque: Boolean)
class Floor() : Tile('.', 15, false, false)
class Cave() : Tile('#', 7, false, true)
class Lava() : Tile('^', 9, false, false)
class Water() : Tile('~', 14, false, false)
object Empty : Tile(' ', 0, false, false)
object Hero : Tile('@', 11, false, false)

fun RenderScope.render(t: Tile, isInverted: Boolean) {
    if (isInverted) invert()
    color(t.colorIndex) { text(t.glyph) }
    clearInvert()
}

val HEIGHT = world.size
val WIDTH = world[0].size

fun main() = session(

    terminal = listOf(
        { SystemTerminal() },
        { VirtualTerminal.create(terminalSize = TerminalSize(WIDTH, HEIGHT)) }
    ).firstSuccess(),
    clearTerminal = true,
) {
    val xMaxIndex = WIDTH - 1
    val yMaxIndex = HEIGHT - 1
    var xView by liveVarOf(0 xy xMaxIndex / 2)
    var yView by liveVarOf(0 xy yMaxIndex / 2)
    var xhero by liveVarOf(10)
    var yhero by liveVarOf(1)
    val padRight = (WIDTH - WIDTH / 2) / 2
    val padTop = (HEIGHT - HEIGHT / 2) / 2
    var blinkOn by liveVarOf(false)
    section {
        textLine("Move hero with w a s d or Q to quit")
    }.run()
    section {
        // section is rendered on each move

        for ((indexY, row) in world.withIndex()) {
            for ((indexX, _) in row.withIndex()) {
                world[indexY][indexX].isVisible = false // hide all tiles - visible radius is based on hero position
            }
        }

        // current view where tiles can be updated
        val view = world.slice(yView.min..yView.max).map {
            it.slice(xView.min..xView.max).toMutableList()
        }

        RayCast.renderHeroRadius(xhero, yhero, view, 6)

        repeat(padTop) { textLine() }

        shiftRight(padRight) {
            cyan {
                bordered(BorderCharacters.CURVED, paddingLeftRight = 1, paddingTopBottom = 1) {
                    view.mapIndexed { y, rows ->
                        rows.mapIndexed { x, tile ->
                            if (yhero == y && xhero == x) render(Hero, false)
                            else if (tile.isVisible) render(tile, if (tile is Lava) blinkOn else false)
                            else text(Empty.glyph)
                        }
                        textLine()
                    }
                }
            }
        }
    }
        .onFinishing { blinkOn = false }
        .runUntilKeyPressed(Keys.Q_UPPER) {
            addTimer(Duration.ofMillis(1000), repeat = true) { blinkOn = !blinkOn }
            onKeyPressed {
                when (key) {
                    Keys.W -> {
                        // Can move across Floor tiles only
                        if (world[yView.min + yhero - 1][xView.min + xhero] !is Floor) return@onKeyPressed

                        // range prior to moving hero used to scroll top or bottom position
                        val yMinPrev = yView.min

                        // the hero is "fixed" to middle of the view unless near the edges where the view is "fixed"
                        yView = if (yhero == yMaxIndex / 4) decView(yView, 0) else yView

                        // the hero is allowed to move to the edges when the view is scrolled to max range
                        yhero = (if (yView.max == yMaxIndex || yMinPrev == 0) (yhero - 1).coerceAtLeast(0) else yhero)
                    }

                    Keys.S -> {
                        if (world[yView.min + yhero + 1][xView.min + xhero] !is Floor) return@onKeyPressed

                        val yPrev = yView.max

                        yView = if (yhero == yMaxIndex / 4) incView(yView, yMaxIndex) else yView

                        yhero =
                            (if (yView.min == 0 || yPrev == yMaxIndex) (yhero + 1).coerceAtMost(yMaxIndex) else yhero)
                    }

                    Keys.A -> {
                        if (world[yView.min + yhero][xView.min + xhero - 1] !is Floor) return@onKeyPressed

                        val xPrev = xView.min

                        xView = if (xhero == xMaxIndex / 4) decView(xView, 0) else xView

                        xhero = (if (xView.max == xMaxIndex || xPrev == 0) (xhero - 1).coerceAtLeast(0) else xhero)
                    }

                    Keys.D -> {
                        if (world[yView.min + yhero][xView.min + xhero + 1] !is Floor) return@onKeyPressed

                        val xPrev = xView.max

                        xView = if (xhero == xMaxIndex / 4) incView(xView, xMaxIndex) else xView

                        xhero =
                            (if (xView.min == 0 || xPrev == xMaxIndex) (xhero + 1).coerceAtMost(xMaxIndex) else xhero)
                    }
                }
            }
        }
}
