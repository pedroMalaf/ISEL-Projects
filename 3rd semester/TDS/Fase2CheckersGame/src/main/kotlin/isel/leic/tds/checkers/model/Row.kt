package isel.leic.tds.checkers.model

/**
 * Class that represents the Rows in the board game Checkers
 */
@JvmInline
value class Row(val index: Int) {

    /**
     * Code executed when instantiating the class Square
     */
    init {
        require(index < BOARD_DIM) { "Invalid row index: $index" }
    }

    /**
     * Value that represents the digit of the row
     */
    val digit get() = (BOARD_DIM - index).toString().first()

    companion object {
        /**
         * List that has all the rows in the board of the game
         */
        val values: List<Row> by lazy { List(BOARD_DIM) { Row(it) } }
    }
}

/**
 * Verifies if the char that extends the method has the necessary property for the
 * creation of a Row, if not it will return null
 * @return A Row or null
 */
fun Char.toRowOrNull(): Row? {
    val index = BOARD_DIM - (this - '1') - 1
    return if (index in 0..<BOARD_DIM) Row(index) else null
}
