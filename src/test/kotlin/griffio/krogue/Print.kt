package griffio.krogue

fun List<MutableList<Tile>>.toFixture(): String = this.joinToString("\n") {
    it.joinToString("") { tile ->
        if (tile.isVisible) tile.glyph.toString() else "s"
    }
}
