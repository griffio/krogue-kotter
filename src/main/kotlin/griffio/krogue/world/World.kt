package griffio.krogue.world

import java.time.LocalDateTime

interface World {
    companion object {
        var date: () -> LocalDateTime = { LocalDateTime.now() }
    }
}

fun main() {

    var current = World
    println(current.date())

    World.date = { LocalDateTime.now().minusDays(1) }
    println(current.date())
}
