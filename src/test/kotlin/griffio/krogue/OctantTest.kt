package griffio.krogue

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OctantTest {

    private val y11x15 = """
        ###############
        #.............#
        #.............#
        #.............#
        #.............#
        #......@......#
        #.............#
        #.............#
        #.............#
        #.............#
        ###############
    """.trimIndent().lines()

    data class Octant(val x: Int, val y: Int)

    fun translateToOctant(row: Int, col: Int, octant: Int): Octant {
        return when (octant) {
            0 -> Octant(col, -row)
            1 -> Octant(row, -col)
            2 -> Octant(row, col)
            3 -> Octant(col, row)
            4 -> Octant(-col, row)
            5 -> Octant(-row, col)
            6 -> Octant(-row, -col)
            7 -> Octant(-col, -row)
            else -> error("octant not between 0-7")
        }
    }

    @Test
    fun octant() {
        // generate sequence https://en.wikipedia.org/wiki/Combination
        fun distances(distance: Int): List<Pair<Int, Int>> {
            var col = 0
            var row = 1
            val result = mutableListOf<Pair<Int, Int>>()
            while (row < distance) {
                println("col $col row $row")
                result.add(col to row)
                col += 1
                if (col == row + 1) {
                    col = 0
                    row++
                    println("---")
                }
            }
            return result
        }

        val view = y11x15.map { it.map(::charToTile).toMutableList() }
        val heroX = 7
        val heroY = 5
        val distance = 6
        val translated =
            (1 until distance)
                .flatMap { row ->
                    (0..row).flatMap { col ->
                        println("$col $row")
                        (0..0).map { oct ->
                            translateToOctant(row, col, oct)
                        }
                    }
                }

        for (octant in translated) {
            val x = heroX + octant.x
            val y = heroY + octant.y
            view[y][x].isVisible = true
        }

        view[5][7].isVisible = true
        val result = view.toFixture()

        assertEquals(
            """
            sssssss######ss
            sssssss.....sss
            sssssss....ssss
            sssssss...sssss
            sssssss..ssssss
            sssssss@sssssss
            sssssssssssssss
            sssssssssssssss
            sssssssssssssss
            sssssssssssssss
            sssssssssssssss
            """.trimIndent(), result
        )
    }


}
