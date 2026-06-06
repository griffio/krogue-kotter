package griffio.krogue

enum class GameStatus { PLAYING, WON, LOST }

// Holds the regenerable game world so a new game can be started without
// restarting the JVM. The top-level `world` was previously a `val` computed
// once at class-load, which made restart (and, later, descending a level)
// impossible.
class GameState {
    var world: List<MutableList<Tile>> = generateWorld()
        private set

    val monsters = mutableListOf<Monster>()

    init {
        spawnMonsters()
    }

    val height get() = world.size
    val width get() = world[0].size

    fun regenerate() {
        world = generateWorld()
        spawnMonsters()
    }

    // Drop monsters onto random Floor tiles. Overlaps are possible but harmless
    // (movement resolves them); deeper-level scaling arrives in a later milestone.
    private fun spawnMonsters(count: Int = 8) {
        monsters.clear()
        val floors = buildList {
            for (y in world.indices) for (x in world[y].indices) {
                if (world[y][x] is Floor) add(x to y)
            }
        }
        if (floors.isEmpty()) return
        repeat(count) {
            val (x, y) = floors.random()
            monsters += Monster(x, y, hp = 3, glyph = 'g', color = 2, attack = 2)
        }
    }
}
