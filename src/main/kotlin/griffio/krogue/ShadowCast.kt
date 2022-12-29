package griffio.krogue

import kotlin.math.ceil

//  http://roguebasin.roguelikedevelopment.org/index.php?title=FOV_using_recursive_shadowcasting_-_improved
object ShadowCast {

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

    fun renderHeroRadius(heroX: Int, heroY: Int, view: List<MutableList<Tile>>, radius: Int) {
        for (octantCoordinate in octantCoordinates) {
            castLight(view, heroX, heroY, radius.toDouble(), 1, 1.0, 0.0, octantCoordinate)
        }
    }

    private fun castLight(
        view: List<MutableList<Tile>>, heroX: Int, heroY: Int, viewRadius: Double,
        startColumn: Int, leftSlope: Double, rightSlope: Double, octant: Octant,
    ) {
        // Used for distance test.
        var updatableLeftViewSlope = leftSlope
        val viewRadiusSq = viewRadius * viewRadius
        val viewCeiling = ceil(viewRadius).toInt()

        // Set true if the previous cell we encountered was blocked.
        var prevWasBlocked = false

        // As an optimization, when scanning past a block we keep track of the
        // rightmost corner (bottom-right) of the last one seen.  If the next cell
        // is empty, we can use this instead of having to compute the top-right corner
        // of the empty cell.
        var savedRightSlope = -1.0
        val viewWidth: Int = view[0].size
        val viewHeight: Int = view.size

        // Outer loop: walk across each column, stopping when we reach the visibility limit.
        for (currentCol in startColumn..viewCeiling) {

            // Inner loop: walk down the current column.  We start at the top, where X==Y.`z
            // TODO: we waste time walking across the entire column when the view area
            //   is narrow.  Experiment with computing the possible range of cells from
            //   the slopes, and iterate over that instead.
            for (yc in currentCol downTo 0) {
                // Translate local coordinates to grid coordinates.  For the various octants
                // we need to invert one or both values, or swap X for Y.
                val viewX: Int = heroX + currentCol * octant.xx + yc * octant.xy
                val viewY: Int = heroY + currentCol * octant.yx + yc * octant.yy

                // Range-check the values.  This lets us avoid the slope division for blocks
                // that are outside the grid.
                //
                // Note that, while we will stop at a solid column of blocks, we do always
                // start at the top of the column, which may be outside the grid if we're (say)
                // checking the first octant while positioned at the north edge of the map.
                if (viewX < 0 || viewX >= viewWidth || viewY < 0 || viewY >= viewHeight) {
                    continue
                }

                // Compute slopes to corners of current block.  We use the top-left and
                // bottom-right corners.  If we were iterating through a quadrant, rather than
                // an octant, we'd need to flip the corners we used when we hit the midpoint.
                //
                // Note these values will be outside the view angles for the blocks at the
                // ends -- left value > 1, right value < 0.
                val leftBlockSlope = (yc + 0.5) / (currentCol - 0.5)
                val rightBlockSlope = (yc - 0.5) / (currentCol + 0.5)

                // Check to see if the block is outside our view area.  Note that we allow
                // a "corner hit" to make the block visible.  Changing the tests to >= / <=
                // will reduce the number of cells visible through a corner (from a 3-wide
                // swath to a single diagonal line), and affect how far you can see past a block
                // as you approach it.  This is mostly a matter of personal preference.
                if (rightBlockSlope > updatableLeftViewSlope) {
                    // Block is above the left edge of our view area; skip.
                    continue
                } else if (leftBlockSlope < rightSlope) {
                    // Block is below the right edge of our view area; we're done.
                    break
                }

                // This cell is visible, given infinite vision range.  If it's also within
                // our finite vision range, light it up.
                //
                // To avoid having a single lit cell poking out N/S/E/W, use a fractional
                // viewRadius, e.g. 8.5.
                //
                // TODO: we're testing the middle of the cell for visibility.  If we tested
                //  the bottom-left corner, we could say definitively that no part of the
                //  could reduce iteration at the corners.
                //  cell is visible, and reduce the view area as if it were a wall.  This
                val distanceSquared = (currentCol * currentCol + yc * yc).toDouble()

                val tile = view[viewY][viewX]
                if (distanceSquared <= viewRadiusSq) {
                    tile.isVisible = true
                }
                val curBlocked = tile.isOpaque
                if (prevWasBlocked) {
                    if (curBlocked) {
                        // Still traversing a column of walls.
                        savedRightSlope = rightBlockSlope
                    } else {
                        // Found the end of the column of walls.  Set the left edge of our
                        // view area to the right corner of the last wall we saw.
                        prevWasBlocked = false
                        updatableLeftViewSlope = savedRightSlope
                    }
                } else {
                    if (curBlocked) {
                        // Found a wall.  Split the view area, recursively pursuing the
                        // part to the left.  The leftmost corner of the wall we just found
                        // becomes the right boundary of the view area.
                        //
                        // If this is the first block in the column, the slope of the top-left
                        // corner will be greater than the initial view slope (1.0).  Handle
                        // that here.
                        if (leftBlockSlope <= updatableLeftViewSlope) {
                            castLight(
                                view, heroX, heroY, viewRadius, currentCol + 1,
                                updatableLeftViewSlope, leftBlockSlope, octant
                            )
                        }

                        // Once that's done, we keep searching to the right (down the column),
                        // looking for another opening.
                        prevWasBlocked = true
                        savedRightSlope = rightBlockSlope
                    }
                }
            }

            // Open areas are handled recursively, with the function continuing to search to
            // the right (down the column).  If we reach the bottom of the column without
            // finding an open cell, then the area defined by our view area is completely
            // obstructed, and we can stop working.
            if (prevWasBlocked) {
                break
            }
        }

    }
}
