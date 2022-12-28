package griffio.krogue

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RayCastTest {

    private val v11x13 = """
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
        val view = v11x13.map { it.map(::charToTile).toMutableList() }
        RayCast.renderHeroRadius(6, 5, view, 5)
        view[5][6].isVisible = true
        val result = (view.joinToString("\n") {
            it.joinToString("") { tile ->
                if (tile.isVisible) tile.glyph.toString() else "s"
            }
        })

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
            """.trimIndent(), result)

    }
}
