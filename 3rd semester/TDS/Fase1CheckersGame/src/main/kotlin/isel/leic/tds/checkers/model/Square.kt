package isel.leic.tds.checkers.model

/**
 * Class that represents the squares in the board game Checkers
 * @property row The row of the square
 * @property column The column of the square
 */
data class Square private constructor(val row: Row, val column: Column) {

    // getter for the index of the square
    val index: Int
        get() = row.index * BOARD_DIM + column.index
    // getter to verify if the square is black or not
    val black: Boolean
        get() = (row.index + column.index) % 2 == 1

    init {
        if (index !in 0..<BOARD_DIM * BOARD_DIM) {
            throw IllegalArgumentException("Invalid: $index")
        }
    }

    /**
     * Method that returns the string representation of the square
     */
    override fun toString(): String = "${row.digit}${column.symbol}"

    companion object {
        val blackSquares get() = values.filter { it.black }

        val values: List<Square>

        init {
            // Lista para armazenar 'Square'
            val tempList = mutableListOf<Square>()
            for (row in Row.values) {
                for (column in Column.values) {
                    tempList.add(Square(row, column))
                }
            }
            // Converte
            values = tempList.toList()
        }

        operator fun invoke(row: Row, column: Column) = values.find { it.row == row && it.column == column } ?: error("Invalid square")
    }
}

/**
 * Converts a string to a square, if it is not possible it will return null
 * @return A Square or null
 */
fun String.toSquareOrNull(): Square? {
    if (length != 2) return null
    val row = get(0).toRowOrNull() ?: return null
    val col = get(1).toColumnOrNull() ?: return null
    return Square(row, col)
}

/**
 * Converts a string to a square, if it is not possible it will throw an exception
 */
fun String.toSquare(): Square = toSquareOrNull() ?: throw IllegalArgumentException("Invalid: $this" )
