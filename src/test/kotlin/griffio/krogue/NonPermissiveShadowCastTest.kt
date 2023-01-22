package griffio.krogue

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NonPermissiveShadowCastTest {

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
        NonPermissiveShadowCast.renderHeroRadius(6, 5, view, 5)
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
        NonPermissiveShadowCast.renderHeroRadius(6, 3, view, 3)
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
        NonPermissiveShadowCast.renderHeroRadius(11, 5, view, 6)

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

    private val y7x15 = """
        #############
        #...........#
        #.....@.....#
        #...........#
        #...........#
        #...........#
        #...........#
        #...........#
        #############
    """.trimIndent().lines()

    @Test
    fun castLightRadiusOffCentre() {
        val view = y7x15.map { it.map(::charToTile).toMutableList() }
        NonPermissiveShadowCast.renderHeroRadius(6, 2, view, 3)
        view[2][6].isVisible = true
        val result = view.toFixture()

        assertEquals(
            """
                sss#######sss
                sss.......sss
                sss...@...sss
                sss.......sss
                sss.......sss
                ssss.....ssss
                sssssssssssss
                sssssssssssss
                sssssssssssss
            """.trimIndent(), result
        )

    }

    private val y11x23OffCentre = """
        #######################
        #.....................#
        #.....................#
        #.......#.....#.......#
        #.....................#
        #.......#@....#.......#
        #.....................#
        #.......#.....#.......#
        #.....................#
        #.....................#
        #######################
    """.trimIndent().lines()

    @Test
    fun castLightPillarsOffCentre() {
        val view = y11x23OffCentre.map { it.map(::charToTile).toMutableList() }
        NonPermissiveShadowCast.renderHeroRadius(9, 5, view, 6)

        val result = view.toFixture()

        assertEquals(
            """
                sssss#########sssssssss
                sssss..........ssssssss
                ssssss.........ssssssss
                sssssss.#.....#.sssssss
                ssssssss........sssssss
                ssssssss#@....#ssssssss
                ssssssss........sssssss
                sssssss.#.....#.sssssss
                ssssss.........ssssssss
                sssss..........ssssssss
                sssss#########sssssssss
            """.trimIndent(), result
        )
    }

}
