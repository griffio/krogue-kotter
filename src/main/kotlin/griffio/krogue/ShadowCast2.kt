package griffio.krogue

//  http://roguebasin.roguelikedevelopment.org/index.php?title=FOV_using_recursive_shadowcasting_-_improved
object ShadowCast2 {
    private data class Octant(val xx: Int, val xy: Int, val yx: Int, val yy: Int)

    private val octantCoordinates = listOf(
        Octant(0, -1, 1, 0),
        Octant(1, 0, 0, -1),
        Octant(1, 0, 0, 1),
        Octant(0, 1, 1, 0),
        Octant(0, 1, -1, 0),
        Octant(-1, 0, 0, 1),
        Octant(-1, 0, 0, -1),
        Octant(0, -1, -1, 0)
    )
    private val mult = listOf(
        Pair(1, 0), Pair(0, -1), Pair(-1, 0), Pair(0, 1),
        Pair(1, -1), Pair(-1, -1), Pair(-1, 1), Pair(1, 1)
    )

    fun renderHeroRadius(heroX: Int, heroY: Int, view: List<MutableList<Tile>>, radius: Int) {
        for (octantCoordinate in octantCoordinates) {
            shadowcast(view, heroX, heroY, radius, octantCoordinate)
        }
    }

    private fun shadowcast(view: List<MutableList<Tile>>, heroX: Int, heroY: Int, radius: Int, octant: Octant) {
        shadowcastOctant(view, heroX, heroY, radius, octant)
    }

    private fun shadowcastOctant(
        view: List<MutableList<Tile>>,
        heroX: Int,
        heroY: Int,
        radius: Int,
        octant: Octant
    ) {
        var prev = 1

        for (slope in 1 until radius) {
            val newStart = shadowCastSlope(view, heroX, heroY, octant, slope, prev)
            if (newStart < prev) {
                break
            }
            prev = newStart
        }
    }

    private fun shadowCastSlope(
        view: List<MutableList<Tile>>,
        heroX: Int,
        heroY: Int,
        octant: Octant,
        slope: Int,
        start: Int,
    ): Int {
        val viewWidth: Int = view[0].size -1
        val viewHeight: Int = view.size -1
        var x = heroX
        var y = heroY
        var blocked = false

        for (i in start until slope + 1) {
            x = heroX + start * octant.xx + i * octant.xy
            y = heroY + start * octant.yx + i * octant.yy
            view[y][x].isVisible = true
        }

        if (blocked) {
            return slope
        }

        return start
    }
}
