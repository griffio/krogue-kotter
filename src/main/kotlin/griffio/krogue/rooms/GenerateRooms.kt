package griffio.krogue.rooms

import kotlin.math.max
import kotlin.math.min

const val MIN_DIM = 2
const val MAX_DIM = 9
val ROOM_DISTANCES = arrayOf(1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 5)
val dimensions = (60..100)
var tiles = mutableListOf<MutableList<Char>>()

var rooms = mutableListOf<Room>()

fun generateRooms(): List<List<Char>> {
    val width = dimensions.random()
    val height = dimensions.random()
    tiles = MutableList(height) { MutableList(width) { '#' } }

    var top = (1..height - MIN_DIM - 2).random()
    var bottom = top + (MIN_DIM..min(MAX_DIM, height - top - 2)).random()
    var left = (1..width - MIN_DIM - 2).random()
    var right = left + (MIN_DIM..min(MAX_DIM, width - left - 2)).random()

    digRoom(top, bottom, left, right)

    val loop = (10..350).random()

    for (i in 1..loop) {

        val from = rooms.random()
        val distance = ROOM_DISTANCES.random()

        when ((0..3).random()) {
            0 -> {
                bottom = from.top - distance
                if (bottom - MIN_DIM <= 0) continue
                val heightDim = (MIN_DIM..min(MAX_DIM, bottom - 1)).random()
                top = bottom - heightDim

                val widthDim = (MIN_DIM..MAX_DIM).random()
                left = (max(1, from.left - widthDim)..min(width - widthDim - 2, from.right)).random()
                right = left + widthDim
                digRoom(top, bottom, left, right)
                val x = (max(left, from.left)..min(right, from.right)).random()
                for (y in (bottom..from.top)) {
                    tiles[y][x] = '.'
                }

                if (from.top - bottom == 2 && tiles[bottom + 1][x - 1] == '#' && tiles[bottom + 1][x + 1] == '#') {
                    tiles[bottom + 1][x] = '^'
                }
            }

            1 -> {
                top = from.bottom + distance
                val heightDim = (MIN_DIM..MAX_DIM).random()
                bottom = top + heightDim
                if (bottom >= height - 1) continue

                val widthDim = (MIN_DIM..MAX_DIM).random()
                left = (max(1, from.left - widthDim)..min(width - widthDim - 2, from.right)).random()
                right = left + widthDim
                digRoom(top, bottom, left, right)
                val x = (max(left, from.left)..min(right, from.right)).random()
                for (y in (bottom..from.top)) {
                    tiles[y][x] = '.'
                }

                if (from.top - bottom == 2 && tiles[top - 1][x - 1] == '#' && tiles[top - 1][x + 1] == '#') {
                    tiles[top - 1][x] = '~'
                }
            }

            2 -> {
                right = from.left - distance
                val widthDim = (MIN_DIM..MAX_DIM).random()
                left = right - widthDim
                if (left <= 0) continue

                val heightDim = (MIN_DIM..MAX_DIM).random()

                top = (max(1, from.top - heightDim)..min(height - heightDim - 2, from.bottom)).random()
                bottom = top + heightDim

                digRoom(top, bottom, left, right)

                val y = (max(top, from.top)..min(bottom, from.bottom)).random()
                for (x in (right..from.left)) {
                    tiles[y][x] = '.'
                }

                if (from.left - right == 2 && tiles[y][right + 1] == '#' && tiles[y][right + 1] == '#') {
                    tiles[y][right + 1] = '~'
                }
            }

            3 -> {
                left = from.right + distance
                val widthDim = (MIN_DIM..MAX_DIM).random()
                right = left + widthDim
                if (right >= width - 1) continue

                val heightDim = (MIN_DIM..MAX_DIM).random()
                top = (max(1, from.top - heightDim)..min(height - heightDim - 2, from.bottom)).random()
                bottom = top + heightDim

                digRoom(top, bottom, left, right)
                val y = (max(top, from.top)..min(bottom, from.bottom)).random()
                for (x in (from.right..left)) {
                    tiles[y][x] = '.'
                }

                if (left - from.right == 2 && tiles[y][left - 1] == '#' && tiles[y][left - 1] == '#')
                    tiles[y][left - 1] = '~'
            }
        }

    }

    return tiles
}

fun digRoom(top: Int, bottom: Int, left: Int, right: Int) {

    for (x in left..right) {
        for (y in top..bottom) {
            if (tiles[y][x] != '#') return
        }
    }

    for (x in left..right) {
        for (y in top..bottom) {
            tiles[y][x] = '.'
        }
    }

    rooms += Room(top, bottom, left, right)
}
