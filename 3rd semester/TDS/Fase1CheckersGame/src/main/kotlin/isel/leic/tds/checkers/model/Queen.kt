package isel.leic.tds.checkers.model

import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.component4

/**
 * Represents the queen piece
 * @property player the player that owns the piece
 */
class Queen(player: Player) : Piece(player) {
/*
    private val listOfEnemies = mutableListOf<Square>()

    private fun intermediateCalculationsProcess(from: Square, to: Square): List<Int> {
        // difference in the col and row movements
        val rowDifference = to.row.index - from.row.index
        val colDifference = to.column.index - from.column.index

        // calculate the steps that should be given (positive or negative)
        val rowStep = if (rowDifference > 0) 1 else -1
        val colStep = if (colDifference > 0) 1 else -1

        // gets the current row and col depending on the range
        var row = (from.row.index + rowStep)
        var col = (from.column.index - colStep)

        return listOf(row, col, rowStep, colStep)
    }

    override fun canMove(from: Square, to: Square, board: Board): Boolean {
        val rowAndCol = intermediateCalculationsProcess(from, to)
        var row = rowAndCol[0]
        var col = rowAndCol[1]

        // until the row and the col don't reach the square [to]
        while (row != to.row.index && col != to.column.index) {
            val r = Row.values[row - 1]
            val c = Column.values[col - 1]
            val s = Square(r, c)
            if (board.playerTurn == board.moves[s]?.player) return false

            row += rowAndCol[2]
            col += rowAndCol[3]
        }
        return board.moves[to]?.player != board.playerTurn
    }

    override fun canCapture(from: Square, to: Square, board: Board): Boolean {
        val rowAndCol = intermediateCalculationsProcess(from, to)
        var row = rowAndCol[0]
        var col = rowAndCol[1]
        var enemyFound = false

        row += rowAndCol[2]
        col += rowAndCol[3]

        var r = Row.values[row - 1]
        var c = Column.values[col - 1]
        var s = Square(r, c)

        if(from.row.index < to.row.index){
            row -= rowAndCol[2]
            col -= rowAndCol[3]
        } else {
            row += rowAndCol[2]
            col += rowAndCol[3]
        }
            // until the row and the col don't reach the square [to]
        while (s != to) {
            r = Row.values[row - 1]
            c = Column.values[col - 1]
            s = Square(r, c)

            when {
                board.moves[from]?.player == board.moves[s]?.player -> return false         // founds own piece
                //founds an enemy piece and if it is the first enemy the flag enemyFound will be true
                board.moves[s]?.player != null ->
                    if (enemyFound) return false
                    else {
                        enemyFound = true
                        listOfEnemies.add(s)
                    }

                enemyFound -> enemyFound = false        // redefine the value to false again to catch another enemy
            }
            row += rowAndCol[2]
            col += rowAndCol[3]
        }

        return listOfEnemies.isNotEmpty() && board.moves[to]?.player != board.moves[from]?.player
    }

    fun captureTheEnemies(board: Board, from: Square, to: Square): Moves {
        val mvs = board.moves + (to to this) + (from to null) + (listOfEnemies.associateWith { null })
        return mvs
    }
 */

    private fun intermediateCalculationsProcess(from: Square, to: Square): List<Int> {
        val rowDifference = to.row.index - from.row.index           // acho que aqui da para tirar o .index
        val colDifference = to.column.index - from.column.index
        val rowStep = if (rowDifference > 0) 1 else -1
        val colStep = if (colDifference > 0) 1 else -1
        return listOf(from.row.index + rowStep, from.column.index + colStep, rowStep, colStep)
    }

    /**
     * Verifies if the queen can move to the square [to]
     * @param from the square from which the queen is moving
     * @param to the square to which the queen is moving
     * @param board the board with the moves
     * @return true if the queen can move to the square [to], false otherwise
     */
    override fun canMove(from: Square, to: Square, board: Board): Boolean {
        val (row, col, rowStep, colStep) = intermediateCalculationsProcess(from, to)
        return generateSequence(Pair(row, col)) { (r, c) ->
            Pair(r + rowStep, c + colStep)
        }.takeWhile { (r, c) ->
            r != to.row.index && c != to.column.index
        }.all { (r, c) ->
            val s = Square(Row.values[r], Column.values[c])
            board.moves[s]?.player != board.playerTurn
        } && board.moves[to]?.player != board.playerTurn
    }

    /**
     * Verifies if the queen can capture a piece with the move to the square [to]
     * @param from the square from which the queen is moving
     * @param to the square to which the queen is moving
     * @param board the board with the moves
     * @return true if the queen can capture a piece with the move [to], false otherwise
     */
    override fun canCapture(from: Square, to: Square, board: Board): Boolean {
        val (row, col, rowStep, colStep) = intermediateCalculationsProcess(from, to)
        val path = generateSequence(Pair(row, col)) { (r, c) ->
            Pair(r + rowStep, c + colStep)
        }.takeWhile { (r, c) ->
            r != to.row.index && c != to.column.index
        }.toList()

        val enemies = path.filter { (r, c) ->
            val s = Square(Row.values[r], Column.values[c])
            board.moves[s]?.player != null && board.moves[s]?.player != board.playerTurn
        }

        return enemies.size == 1 && board.moves[to]?.player != board.playerTurn
    }

    /**
     * Captures the piece in the square [to] and updates the board
     * @param from the square from which the queen is moving
     * @param to the square to which the queen is moving
     * @param board the board with the moves
     * @return the updated board with the captured piece
     */
    override fun capture(from: Square, to: Square, board: Board): Moves {
        val (row, col, rowStep, colStep) = intermediateCalculationsProcess(from, to)
        val path = generateSequence(Pair(row, col)) { (r, c) ->
            Pair(r + rowStep, c + colStep)
        }.takeWhile { (r, c) ->
            r != to.row.index && c != to.column.index
        }.toList()

        val enemy = path.firstOrNull { (r, c) ->
            val s = Square(Row.values[r], Column.values[c])
            board.moves[s]?.player != null && board.moves[s]?.player != board.playerTurn
        } ?: return board.moves

        val (enemyRow, enemyCol) = enemy
        val enemySquare = Square(Row.values[enemyRow], Column.values[enemyCol])

        return board.moves + (to to this) + (from to null) + (enemySquare to null)
    }
}
