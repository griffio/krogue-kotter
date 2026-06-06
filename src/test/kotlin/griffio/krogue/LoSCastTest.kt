package griffio.krogue

import griffio.krogue.LoSCast.lineBetween
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LoSCastTest {

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
        //LoSCast.renderHeroRadius(6, 5, view, 3)

        val h = LoSCast.Point(6, 5)
        val w = LoSCast.Point(5, 0)

        val b6 = LoSCast.Point(7, 6)
        val b5 = LoSCast.Point(7, 5)
        val b4 = LoSCast.Point(7, 4)
        val b3 = LoSCast.Point(7, 3)
        val b2 = LoSCast.Point(7, 2)
        val b1 = LoSCast.Point(7, 1)
        println(b6.lineBetween(h, w))
        println(b5.lineBetween(h, w))
        println(b4.lineBetween(h, w))
        println(b3.lineBetween(h, w))
        println(b2.lineBetween(h, w))
        println(b1.lineBetween(h, w))

        view[5][6].isVisible = true
        val result = view.toFixture()

        assertEquals(
            """
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
        #.........#...........#
        #..........@..........#
        #######################
    """.trimIndent().lines()

    @Test
    fun castLightPillars() {
        val view = y11x23.map { it.map(::charToTile).toMutableList() }
        RayCast.renderHeroRadius(11, 4, view, 5)
        view[4][11].isVisible = true
        val result = view.toFixture()

        assertEquals(
            """
                #######################
                #.....................#
                #........s............#
                #.........#...........#
                #..........@..........#
                #######################
            """.trimIndent(), result
        )
    }
}
