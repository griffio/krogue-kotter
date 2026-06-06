package griffio.krogue

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
import griffio.krogue.rooms.TOTAL_CASH
import griffio.krogue.rooms.generateRooms
import kotlin.math.abs

fun charToTile(char: Char): Tile {
    return when (char) {
        '#' -> Cave()
        '.' -> Floor()
        '^' -> Lava()
        '~' -> Water()
        '£' -> Cash()
        '@' -> Hero
        else -> error("Unknown tile: $char")
    }
}

fun generateWorld(): List<MutableList<Tile>> = generateRooms().map { line ->
    line.map(::charToTile).toMutableList()
}

val gameState = GameState()

const val HERO_ATTACK = 2

data class View(val min: Int, val max: Int)

infix fun Int.xy(that: Int): View = View(this, that)

fun incView(current: View, inc: Int, max: Int) =
    if (current.max < max) current.min + inc xy current.max + inc else current

fun decView(current: View, dec: Int, min: Int) =
    if (current.min > min) current.min - dec xy current.max - dec else current

// https://en.wikipedia.org/wiki/ANSI_escape_code (Standard colors/High-intensity colors)
sealed class Tile(
    val glyph: Char,
    var colorIndex: Int,
    var isVisible: Boolean,
    var isOpaque: Boolean,
    var isTaken: Boolean,
) {
    // Set once a tile has entered the hero's light radius; stays true so
    // previously seen terrain can be drawn dimmed (fog of war).
    var isExplored: Boolean = false
}

class Floor() : Tile('.', 15, false, false, false)
class Cave() : Tile('#', 7, false, true, false)
class Lava() : Tile('^', 9, false, false, false)
class Water() : Tile('~', 14, false, false, false)
class Cash() : Tile('£', 11, false, false, false)
object Empty : Tile(' ', 0, false, false, false)
object Hero : Tile('@', 10, false, false, false)

fun RenderScope.render(t: Tile, isInverted: Boolean) {
    if (isInverted) invert()
    color(t.colorIndex) { text(t.glyph) }
    clearInvert()
}

// Terminal size is fixed for the process from the first generated world.
val HEIGHT = gameState.height
val WIDTH = gameState.width

fun main() = session(
    terminal = listOf(
        { SystemTerminal() },
        { VirtualTerminal.create(terminalSize = TerminalSize(WIDTH - (WIDTH / 3), HEIGHT - (HEIGHT / 3))) }
    ).firstSuccess(),
    clearTerminal = true,
) {
    // var (not val): a regenerated world on restart may have different dimensions.
    var xMaxIndex = WIDTH - 1
    var yMaxIndex = HEIGHT - 1
    var xView by liveVarOf(0 xy xMaxIndex / 2)
    var yView by liveVarOf(0 xy yMaxIndex / 2)
    var xhero by liveVarOf(xMaxIndex / 4)
    var yhero by liveVarOf(yMaxIndex / 4)
    var blinkOn by liveVarOf(false)
    var healthPoints by liveVarOf((8..16).random())
    var cash by liveVarOf(0)
    var status by liveVarOf(GameStatus.PLAYING)

    fun monsterAt(worldX: Int, worldY: Int): Monster? =
        gameState.monsters.firstOrNull { it.x == worldX && it.y == worldY }

    // A monster may step onto any non-opaque, unoccupied, in-bounds tile.
    fun canStep(worldX: Int, worldY: Int, self: Monster): Boolean {
        val tile = gameState.world.getOrNull(worldY)?.getOrNull(worldX) ?: return false
        if (tile.isOpaque) return false
        return gameState.monsters.none { it !== self && it.x == worldX && it.y == worldY }
    }

    // One monster turn: visible monsters chase the hero (orthogonally, dominant
    // axis first); unseen ones wander. Stepping into the hero is a melee attack.
    fun monstersAct() {
        if (status != GameStatus.PLAYING) return
        val heroWX = xView.min + xhero
        val heroWY = yView.min + yhero

        for (m in gameState.monsters) {
            val canSeeHero = gameState.world.getOrNull(m.y)?.getOrNull(m.x)?.isVisible == true
            val steps: List<Pair<Int, Int>> = if (canSeeHero) {
                val dx = (heroWX - m.x).coerceIn(-1, 1)
                val dy = (heroWY - m.y).coerceIn(-1, 1)
                if (abs(heroWX - m.x) >= abs(heroWY - m.y)) listOf(dx to 0, 0 to dy)
                else listOf(0 to dy, dx to 0)
            } else {
                listOf(listOf(-1, 0, 1).random() to listOf(-1, 0, 1).random())
            }

            for ((sx, sy) in steps) {
                if (sx == 0 && sy == 0) continue
                val nx = m.x + sx
                val ny = m.y + sy
                if (nx == heroWX && ny == heroWY) {
                    healthPoints = (healthPoints - m.attack).coerceAtLeast(0)
                    if (healthPoints < 1) status = GameStatus.LOST
                    break
                }
                if (canStep(nx, ny, m)) {
                    m.x = nx
                    m.y = ny
                    break
                }
            }
        }
    }

    fun tryMoveHero(tile: Tile, destX: Int, destY: Int, move: () -> Unit) {

        if (status != GameStatus.PLAYING) return

        // Bump-to-attack: moving into a monster strikes it instead of moving.
        val monster = monsterAt(destX, destY)
        if (monster != null) {
            monster.hp -= HERO_ATTACK
            if (monster.hp <= 0) gameState.monsters.remove(monster)
            monstersAct()
            return
        }

        if (tile is Cash) {
            cash += 1
            tile.isTaken = true
            if (cash == TOTAL_CASH) status = GameStatus.WON
        }
        if (tile is Lava) healthPoints = (healthPoints - 6).coerceAtLeast(0)
        if (tile is Water) healthPoints = (healthPoints - 1).coerceAtLeast(0)
        if (healthPoints < 1) status = GameStatus.LOST
        if (status == GameStatus.PLAYING && !tile.isOpaque) {
            move()
            monstersAct()
        }
    }

    section {
        textLine("Move hero with w a s d or Q to quit")
    }.run()

    section {
        // section is rendered on each move

        if (status != GameStatus.PLAYING) {
            bordered(BorderCharacters.CURVED, paddingLeftRight = 2, paddingTopBottom = 1) {
                if (status == GameStatus.WON) green(isBright = true) { textLine("YOU WIN!") }
                else red(isBright = true) { textLine("YOU DIED") }
                textLine("Score: $cash of $TOTAL_CASH")
                textLine("Press R to restart · Q to quit")
            }
            return@section
        }

        for ((indexY, row) in gameState.world.withIndex()) {
            for ((indexX, _) in row.withIndex()) {
                gameState.world[indexY][indexX].isVisible = false // hide all tiles - visible radius is based on hero position
                if (gameState.world[indexY][indexX].isTaken) {
                    gameState.world[indexY][indexX] = Floor()
                }
            }
        }

        // current view where tiles can be updated
        val view = gameState.world.slice(yView.min..yView.max).map {
            it.slice(xView.min..xView.max).toMutableList()
        }

        ShadowCast.renderHeroRadius(xhero, yhero, view, 8)

        val healthText = "Health: $healthPoints"
        val cashText = "Cash: $cash of $TOTAL_CASH"

        statusPanels(
            leftColor = {
                when {
                    healthPoints < 1 -> red(isBright = true)
                    healthPoints < 6 -> yellow(isBright = true)
                    else -> green(isBright = true)
                }
            },
            leftText = { text(healthText) },
            rightColor = { yellow(isBright = true) },
            rightText = { text(cashText) }
        )
        cyan {
            bordered(BorderCharacters.CURVED, paddingLeftRight = 1, paddingTopBottom = 1) {
                view.mapIndexed { y, rows ->
                    rows.mapIndexed { x, tile ->
                        // view (x, y) -> world coords for monster lookup
                        val monster = if (tile.isVisible) monsterAt(xView.min + x, yView.min + y) else null
                        when {
                            yhero == y && xhero == x -> render(Hero, false)
                            monster != null -> color(monster.color) { text(monster.glyph) }
                            tile.isVisible -> render(tile, if (tile is Lava) blinkOn else false)
                            tile.isExplored -> color(8) { text(tile.glyph) } // dim fog-of-war memory
                            else -> text(Empty.glyph)
                        }
                    }
                    textLine()
                }
            }
        }
    }
        //   }
        .onFinishing { blinkOn = false }
        .runUntilKeyPressed(Keys.Q_UPPER) {
            //addTimer(Duration.ofMillis(1000), repeat = true) { blinkOn = !blinkOn }
            onKeyPressed {
                when (key) {

                    Keys.R_UPPER -> {
                        if (status != GameStatus.PLAYING) {
                            gameState.regenerate()
                            xMaxIndex = gameState.width - 1
                            yMaxIndex = gameState.height - 1
                            xView = 0 xy xMaxIndex / 2
                            yView = 0 xy yMaxIndex / 2
                            xhero = xMaxIndex / 4
                            yhero = yMaxIndex / 4
                            healthPoints = (8..16).random()
                            cash = 0
                            status = GameStatus.PLAYING
                        }
                    }

                    Keys.W -> {
                        // Can move across Floor tiles only
                        tryMoveHero(gameState.world[yView.min + yhero - 1][xView.min + xhero], xView.min + xhero, yView.min + yhero - 1) {
                            // range prior to moving hero used to scroll top or bottom position
                            val yMinPrev = yView.min

                            // the hero is "fixed" to middle of the view unless near the edges where the view is "fixed"
                            yView = if (yhero == yMaxIndex / 4) decView(yView, 1, 0) else yView

                            // the hero is allowed to move to the edges when the view is scrolled to max range
                            yhero =
                                (if (yView.max == yMaxIndex || yMinPrev == 0) (yhero - 1).coerceAtLeast(0) else yhero)
                        }

                    }

                    Keys.S -> {
                        tryMoveHero(gameState.world[yView.min + yhero + 1][xView.min + xhero], xView.min + xhero, yView.min + yhero + 1) {
                            val yPrev = yView.max

                            yView = if (yhero == yMaxIndex / 4) incView(yView, 1, yMaxIndex) else yView

                            yhero =
                                (if (yView.min == 0 || yPrev == yMaxIndex) (yhero + 1).coerceAtMost(yMaxIndex) else yhero)
                        }
                    }

                    Keys.A -> {
                        tryMoveHero(gameState.world[yView.min + yhero][xView.min + xhero - 1], xView.min + xhero - 1, yView.min + yhero) {
                            val xPrev = xView.min

                            xView = if (xhero == xMaxIndex / 4) decView(xView, 1, 0) else xView

                            xhero = (if (xView.max == xMaxIndex || xPrev == 0) (xhero - 1).coerceAtLeast(0) else xhero)
                        }
                    }

                    Keys.D -> {
                        tryMoveHero(gameState.world[yView.min + yhero][xView.min + xhero + 1], xView.min + xhero + 1, yView.min + yhero) {
                            val xPrev = xView.max

                            xView = if (xhero == xMaxIndex / 4) incView(xView, 1, xMaxIndex) else xView

                            xhero =
                                (if (xView.min == 0 || xPrev == xMaxIndex) (xhero + 1).coerceAtMost(xMaxIndex) else xhero)
                        }
                    }
                }
            }
        }
}
