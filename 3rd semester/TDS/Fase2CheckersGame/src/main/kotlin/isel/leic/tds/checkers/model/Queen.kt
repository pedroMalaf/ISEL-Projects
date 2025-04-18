package isel.leic.tds.checkers.model

import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.collections.component4
import kotlin.text.get

/**
 * Represents the queen piece
 * @property player the player that owns the piece
 */
class Queen(player: Player) : Piece(player) {

    private fun intermediateCalculationsProcess(from: Square, to: Square): List<Int> {
        val rowDifference = to.row.index - from.row.index
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
            r in 0 until BOARD_DIM && c in 0 until BOARD_DIM
        }.toList()

        val enemies = path.filter { (r, c) ->
            val s = Square(Row.values[r], Column.values[c])
            board.moves[s]?.player != null && board.moves[s]?.player != board.playerTurn
        }

        return enemies.size == 1 && path.any { (r, c) ->
            val s = Square(Row.values[r], Column.values[c])
            s == to && board.moves[s] == null
        }
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
            r in 0 until BOARD_DIM && c in 0 until BOARD_DIM
        }.toList()

        val enemy = path.firstOrNull { (r, c) ->
            val s = Square(Row.values[r], Column.values[c])
            board.moves[s]?.player != null && board.moves[s]?.player != board.playerTurn
        } ?: return board.moves

        val (enemyRow, enemyCol) = enemy
        val enemySquare = Square(Row.values[enemyRow], Column.values[enemyCol])

        // Ensure the path after the enemy square is clear
        val isPathClear = path.dropWhile { (r, c) ->
            val s = Square(Row.values[r], Column.values[c])
            s != enemySquare
        }.drop(1).all { (r, c) ->
            val s = Square(Row.values[r], Column.values[c])
            s == to || board.moves[s] == null
        }

        return if (isPathClear) {
            board.moves + (to to this) + (from to null) + (enemySquare to null)
        } else {
            board.moves
        }
    }
}
