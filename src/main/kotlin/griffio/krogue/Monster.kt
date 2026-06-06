package griffio.krogue

// Monsters live in WORLD coordinates (not viewport coordinates). The renderer
// maps them into the current view via (x - xView.min, y - yView.min).
class Monster(
    var x: Int,
    var y: Int,
    var hp: Int,
    val glyph: Char,
    val color: Int,
    val attack: Int,
)
