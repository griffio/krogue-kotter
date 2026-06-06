package griffio.krogue

enum class GameStatus { PLAYING, WON, LOST }

// Holds the regenerable game world so a new game can be started without
// restarting the JVM. The top-level `world` was previously a `val` computed
// once at class-load, which made restart (and, later, descending a level)
// impossible.
class GameState {
    var world: List<MutableList<Tile>> = generateWorld()
        private set

    val height get() = world.size
    val width get() = world[0].size

    fun regenerate() {
        world = generateWorld()
    }
}
