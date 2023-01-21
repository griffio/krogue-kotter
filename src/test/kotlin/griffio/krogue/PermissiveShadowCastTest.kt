package griffio.krogue

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PermissiveShadowCastTest {

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
        PermissiveShadowCast.renderHeroRadius(6, 5, view, 5)
        view[5][6].isVisible = true
        val result = view.toFixture()

        assertEquals(
            """
                ssssssss#ssss
                ss.ssss....ss
                ss..sss..#.ss
                s...###.....s
                s...........s
                s.....@.....s
                s...........s
                s.....##....s
                ss#...sss..ss
                ss....ssss.ss
                ssss#ssssssss
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
        PermissiveShadowCast.renderHeroRadius(6, 3, view, 3)
        view[3][6].isVisible = true
        val result = view.toFixture()

        assertEquals(
            """
                ssss#####ssss
                sss.......sss
                sss.......sss
                sss...@...sss
                sss.......sss
                sss.......sss
                ssss#####ssss
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
        PermissiveShadowCast.renderHeroRadius(11, 5, view, 6)

        val result = view.toFixture()

        assertEquals(
            """
                sssssss#########sssssss
                sssssss.........sssssss
                ssssssss.......ssssssss
                sssss...#.....#...sssss
                sssss.............sssss
                ssssssss#..@..#ssssssss
                sssss.............sssss
                sssss...#.....#...sssss
                ssssssss.......ssssssss
                sssssss.........sssssss
                sssssss#########sssssss
            """.trimIndent(), result
        )
    }
}
