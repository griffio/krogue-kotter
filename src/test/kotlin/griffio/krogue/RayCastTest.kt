package griffio.krogue

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RayCastTest {

    private val y11x13 = """
        #############
        #...........#
        #........#..#
        #...###.....#
        #...........#
        #.....@.....#
        #...........#
        #.....##....#
        ###.........#
        #...........#
        #############
    """.trimIndent().lines()

    @Test
    fun castLight() {
        val view = y11x13.map { it.map(::charToTile).toMutableList() }
        RayCast.renderHeroRadius(6, 5, view, 5)
        view[5][6].isVisible = true
        val result = view.toFixture()

        assertEquals(
            """
                sssssss####ss
                sssssss...s.s
                s..ssss..#..s
                #...###.....#
                #...........#
                #.....@.....#
                #...........#
                #.....##....#
                s##...sss...s
                ss....sss...s
                ss####sssssss
            """.trimIndent(), result
        )

    }

    private val y7x13 = """
        #############
        #...........#
        #...........#
        #.....@.....#
        #...........#
        #...........#
        #############
    """.trimIndent().lines()

    @Test
    fun castLightRadius() {
        val view = y7x13.map { it.map(::charToTile).toMutableList() }
        RayCast.renderHeroRadius(6, 3, view, 3)
        view[3][6].isVisible = true
        val result = view.toFixture()

        assertEquals(
            """
                sss#######sss
                ss.........ss
                ss.........ss
                ss....@....ss
                ss.........ss
                ss.........ss
                sss#######sss
            """.trimIndent(), result
        )

    }

    private val y11x23 = """
        #######################
        #.....................#
        #.....................#
        #.......#.....#.......#
        #.....................#
        #.......#..@..#.......#
        #.....................#
        #.......#.....#.......#
        #.....................#
        #.....................#
        #######################
    """.trimIndent().lines()

    @Test
    fun castLightPillars() {
        val view = y11x23.map { it.map(::charToTile).toMutableList() }
        RayCast.renderHeroRadius(11, 5, view, 7)
        view[5][11].isVisible = true
        val result = view.toFixture()

        assertEquals(
            """
                ssssss###########ssssss
                ssss.s...........s.ssss
                sss.................sss
                sss.....#.....#.....sss
                sss.................sss
                ssssssss#..@..#ssssssss
                sss.................sss
                sss.....#.....#.....sss
                sss.................sss
                ssss.s...........s.ssss
                ssssss###########ssssss
            """.trimIndent(), result
        )
    }
}
