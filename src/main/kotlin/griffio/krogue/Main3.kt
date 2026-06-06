import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun fixedPrediction(a: Int, b: Int, c: Int): Int {
    val minAB = a.min(b)
    val maxAB = a.max(b)
    return when (c) {
        in maxAB..c -> minAB
        in c..minAB -> maxAB
        else -> a + b - c
    }
}

fun line(x1: Int, y1: Int, x2: Int, y2: Int) {
    val deltaX = x2 - x1
    val deltaY = y2 - y1
    var y = y1
    for (x in x1..x2) {
        //plot(x, y)
        y += (deltaY / deltaX)
    }
}

//minab < c < max
private fun Int.min(b: Int): Int = kotlin.math.min(this, b)
private fun Int.max(b: Int): Int = kotlin.math.max(this, b)

fun main(args: List<String>) {
    println(fixedPrediction(6, 22, 3))

    val myList: List<Int>? = listOf()
    if (!myList.isNullOrEmpty()) {
        // myList manipulations
    }

    if (myList.orEmpty().isEmpty()) {
        // Compiler thinks myList can be null here
        // But this is not what I want either, I want the extension fun below
    }

    if (myList.isNotEmptyExtension()) {
        // Compiler thinks myList can be null here
        myList.size
    }


    "sf".isNullOrBlank()

    var x: String? = args[0]


    if (x.isNullOrEmpty2()) {
        println(x)
    }
}

@OptIn(ExperimentalContracts::class)
private fun <T> Collection<T>?.isNotEmptyExtension(): Boolean {
    contract {
        returns(true) implies (this@isNotEmptyExtension != null)
    }
    return !this.isNullOrEmpty()
}

fun CharSequence?.isNullOrEmpty2(): Boolean {
    return this == null || this.length == 0
}


object TestInputResourceReader {
    fun readResourceAsText(path: String): String? =
        this::class.java
            .getResourceAsStream(path)
            ?.run { String(readAllBytes()).trimEnd() }
}
