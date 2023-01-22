package griffio.krogue

import java.util.*

// Translated from Shadowcasting in C#
// https://web.archive.org/web/20150905121206/http://blogs.msdn.com/b/ericlippert/archive/2011/12/29/shadowcasting-in-c-part-six.aspx
object NonPermissiveShadowCast {

    private fun <T> translateOrigin(x: Int, y: Int, f: (Int, Int) -> T): (Int, Int) -> T {
        return { a: Int, b: Int -> f(a + x, b + y) }
    }

    private fun <T> translateOctant(f: (Int, Int) -> T, octant: Int): (Int, Int) -> T {
        return when (octant) {
            1 -> { x: Int, y: Int -> f(y, x) }
            2 -> { x: Int, y: Int -> f(-y, x) }
            3 -> { x: Int, y: Int -> f(-x, y) }
            4 -> { x: Int, y: Int -> f(-x, -y) }
            5 -> { x: Int, y: Int -> f(-y, -x) }
            6 -> { x: Int, y: Int -> f(y, -x) }
            7 -> { x: Int, y: Int -> f(x, -y) }
            else -> f
        }
    }

    private fun isInRadius(x: Int, y: Int, length: Int): Boolean {
        return (2 * x - 1) * (2 * x - 1) + (2 * y - 1) * (2 * y - 1) <= 4 * length * length
    }

    data class DirectionVector(val x: Int, val y: Int)

    data class ColumnPortion(
        val x: Int,
        val bottomVector: DirectionVector,
        val topVector: DirectionVector,
    )

    // Takes a circle in the form of a center point and radius, and a function that
    // can tell whether a given cell is opaque. Calls the setFoV action on
    // every cell that is both within the radius and visible from the center.
    fun renderHeroRadius(
        heroX: Int, heroY: Int,
        view: List<MutableList<Tile>>, radius: Int,
    ) {
        val isOpaque = { x: Int, y: Int ->
            view[y][x].isOpaque
        }

        val setVisible = { x: Int, y: Int ->
             view[y][x].isVisible = true
        }

        val opaque = translateOrigin(heroX, heroY, isOpaque)
        val fov = translateOrigin(heroX, heroY, setVisible)

        for (octant in 0..7) {
            fieldOfViewInOctantZero(
                translateOctant(opaque, octant),
                translateOctant(fov, octant),
                radius
            )
        }
    }

    private fun fieldOfViewInOctantZero(
        isOpaque: (Int, Int) -> Boolean,
        setFieldOfView: (Int, Int) -> Unit,
        radius: Int,
    ) {
        val queue: Queue<ColumnPortion> = LinkedList()
        queue.add(ColumnPortion(0, DirectionVector(1, 0), DirectionVector(1, 1)))
        while (queue.isNotEmpty()) {
            val current = queue.remove()
            if (current.x > radius) continue
            computeFoVForColumnPortion(
                current.x,
                current.topVector,
                current.bottomVector,
                isOpaque,
                setFieldOfView,
                radius,
                queue
            )
        }
    }

    private fun computeFoVForColumnPortion(
        x: Int,
        topVector: DirectionVector,
        bottomVector: DirectionVector,
        isOpaque: (Int, Int) -> Boolean,
        setFieldOfView: (Int, Int) -> Unit,
        radius: Int,
        queue: Queue<ColumnPortion>,
    ) {
        // Search for transitions from opaque to transparent or
        // transparent to opaque and use those to determine what
        // portions of the *next* column are visible from the origin.

        // Start at the top of the column portion and work down.

        val topY: Int = if (x == 0) 0
        else {
            val quotient = (2 * x + 1) * topVector.y / (2 * topVector.x)
            val remainder = (2 * x + 1) * topVector.y % (2 * topVector.x)

            if (remainder > topVector.x)
                quotient + 1
            else
                quotient
        }

        // Note that this can find a top cell that is actually entirely blocked by
        // the cell below it consider detecting and eliminating that.


        val bottomY: Int = if (x == 0) 0
        else {
            val quotient: Int = (2 * x - 1) * bottomVector.y / (2 * bottomVector.x)
            val remainder: Int = (2 * x - 1) * bottomVector.y % (2 * bottomVector.x)

            if (remainder >= bottomVector.x)
                quotient + 1
            else
                quotient
        }

        // A more sophisticated algorithm would say that a cell is visible if there is
        // *any* straight line segment that passes through *any* portion of the origin cell
        // and any portion of the target cell, passing through only transparent cells
        // along the way. This is the "Permissive Field Of View" algorithm, and it
        // is much harder to implement.

        var wasLastCellOpaque: Boolean? = null
        var lastCellTopVector = topVector
        for (y in topY downTo bottomY) {
            val inRadius: Boolean = isInRadius(x, y, radius)
            if (inRadius) {
                // The current cell is in the field of view.
                setFieldOfView(x, y)
            }

            // A cell that was too far away to be seen is effectively
            // an opaque cell nothing "above" it is going to be visible
            // in the next column, so we might as well treat it as
            // an opaque cell and not scan the cells that are also too
            // far away in the next column.
            val currentIsOpaque: Boolean = !inRadius || isOpaque(x, y)
            if (wasLastCellOpaque != null) {
                if (currentIsOpaque) {
                    // We've found a boundary from transparent to opaque. Make a note
                    // of it and revisit it later.
                    if (!wasLastCellOpaque) {
                        // The new bottom vector touches the upper left corner of
                        // opaque cell that is below the transparent cell.
                        queue.add(
                            ColumnPortion(
                                x + 1,
                                DirectionVector(
                                    x * 2 - 1, y * 2 + 1
                                ),
                                lastCellTopVector
                            )
                        )
                    }
                } else if (wasLastCellOpaque) {
                    // We've found a boundary from opaque to transparent. Adjust the
                    // top vector so that when we find the next boundary or do
                    // the bottom cell, we have the right top vector.
                    //
                    // The new top vector touches the lower right corner of the
                    // opaque cell that is above the transparent cell, which is
                    // the upper right corner of the current transparent cell.
                    lastCellTopVector = DirectionVector(x * 2 + 1, y * 2 + 1)
                }
            }
            wasLastCellOpaque = currentIsOpaque
        }

        // Make a note of the lowest opaque-->transparent transition, if there is one.
        if (wasLastCellOpaque != null && !wasLastCellOpaque)
            queue.add(ColumnPortion(x + 1, bottomVector, topVector))
    }

}
