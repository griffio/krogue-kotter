package griffio.krogue

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// https://jeremyceri.se/post/roguelike-dev-week-3-part-1/
// Part 5: Field of Vision
object RayCast {

    private val cosTable = LinkedHashMap<Int, Double>().apply {
        for (i in 0 until 360) {
            put(i, 0.0)
        }
    }
    private val sinTable = LinkedHashMap<Int, Double>().apply {
        for (i in 0 until 360) {
            put(i, 0.0)
        }
    }

    init {
        for (i in 0 until 360) {
            val ax = sin(i.toDouble() / (180.0 / PI))
            val ay = cos(i.toDouble() / (180.0 / PI))
            sinTable[i] = ax
            cosTable[i] = ay
        }
    }

    fun renderHeroRadius(heroX: Int, heroY: Int, view: List<MutableList<Tile>>, radius: Int) {
        val worldMaxX = view[0].size - 1
        val worldMaxY = view.size - 1

        for (i in 0 until 360) {
            val ax = sinTable[i]!!
            val ay = cosTable[i]!!

            var x = heroX.toDouble()
            var y = heroY.toDouble()

            for (j in 0..radius) {
                x -= ax
                y -= ay

                val roundedX = x.roundToInt()
                val roundedY = y.roundToInt()

                if (x < 0 || x > worldMaxX || y < 0 || y > worldMaxY) {
                    break
                }

                if (view[roundedY][roundedX].isOpaque) {
                    view[roundedY][roundedX].isVisible = true
                    break
                }

                view[roundedY][roundedX].isVisible = true
            }
        }
    }
}
