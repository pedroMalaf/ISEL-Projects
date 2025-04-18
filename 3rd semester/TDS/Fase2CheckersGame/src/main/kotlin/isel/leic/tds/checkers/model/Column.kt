package isel.leic.tds.checkers.model

/**
 * Class that represents the Columns in the board game Checkers
 * @property index the index of the column
 */
@JvmInline
value class Column(val index: Int) {

    /**
     * Code executed when instantiating the class Square
     */
    init {
        require(index < BOARD_DIM) { "Invalid column index: $index" }
    }

    /**
     * Value that represents the symbol of the column
     */
    val symbol get() = 'a' + index

    companion object {

        /**
         * List that has all the columns in the board of the game
         */
        val values: List<Column> by lazy { List(BOARD_DIM) { Column(it) } }
    }
}

/**
 * Function that verifies if the char that extends the method has the necessary property for the
 * creation of a Column, if not it will return null
 * @return A Column or null
 */
fun Char.toColumnOrNull() =
    if (this in 'a'..'h') Column(this - 'a') else null