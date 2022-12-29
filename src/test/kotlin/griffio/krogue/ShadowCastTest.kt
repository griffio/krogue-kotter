package griffio.krogue

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ShadowCastTest {

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
        ShadowCast.renderHeroRadius(6, 5, view, 5)
        view[5][6].isVisible = true
        val result = (view.joinToString("\n") {
            it.joinToString("") { tile ->
                if (tile.isVisible) tile.glyph.toString() else "s"
            }
        })

        assertEquals(
            """
                sssssssssssss
                sssssss...sss
                ss.ssss..#.ss
                ss..###....ss
                ss.........ss
                s.....@.....s
                ss.........ss
                ss....##...ss
                ss#...ss...ss
                sss...sss.sss
                sssssssssssss
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
        ShadowCast.renderHeroRadius(6, 3, view, 3)
        view[3][6].isVisible = true
        val result = (view.joinToString("\n") {
            it.joinToString("") { tile ->
                if (tile.isVisible) tile.glyph.toString() else "s"
            }
        })

        assertEquals(
            """
               ssssss#ssssss
               ssss.....ssss
               ssss.....ssss
               sss...@...sss
               ssss.....ssss
               ssss.....ssss
               ssssss#ssssss
            """.trimIndent(), result
        )

    }
}
