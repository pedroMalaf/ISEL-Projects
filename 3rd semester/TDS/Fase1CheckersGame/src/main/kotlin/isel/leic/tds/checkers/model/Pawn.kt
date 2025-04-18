package isel.leic.tds.checkers.model

import kotlin.collections.plus
import kotlin.math.abs

/**
 * Represents a pawn piece in the game.
 * @property player The player that owns the piece.
 */
class Pawn(player: Player): Piece(player) {

    /**
     * Verifies if a move can be made.
     * @param from The square where the piece is.
     * @param to The square where the piece wants to move to.
     * @param board The board of the game.
     * @return True if the piece can move to the square, false otherwise.
     */
    override fun canMove(from: Square, to: Square, board: Board): Boolean {
        val rowDiff = to.row.index - from.row.index
        val colDiff = abs(to.column.index - from.column.index)
        return when (player) {
            Player.WHITE -> rowDiff == -1 && colDiff == 1 && board.moves[to] == null
            Player.BLACK -> rowDiff == 1 && colDiff == 1 && board.moves[to] == null
        }
    }

    /**
     * Verifies if a capture can be made with a certain move.
     * @param from The square where the piece is.
     * @param to The square where the piece wants to move to.
     * @param board The board of the game.
     * @return True if the piece can capture the piece in the square, false otherwise.
     */
    override fun canCapture(from: Square, to: Square, board: Board): Boolean {
        val rowDiff = to.row.index - from.row.index
        val colDiff = abs(to.column.index - from.column.index)
        if (colDiff == 2 && board.moves[to] == null) {
            val midRow = (from.row.index + to.row.index) / 2
            val midCol = (from.column.index + to.column.index) / 2
            if (midRow !in Row.values.indices || midCol !in Column.values.indices) return false // sera ?
            val midSquare = Square(Row.values[midRow], Column.values[midCol])
            return when (player) {
                Player.WHITE -> rowDiff == -2 && board.moves[midSquare]?.player == Player.BLACK
                Player.BLACK -> rowDiff == 2 && board.moves[midSquare]?.player == Player.WHITE
            }
        }
        return false
    }

    /**
     * Captures a piece in a certain square.
     * @param from The square where the piece is.
     * @param to The square where the piece wants to move to.
     * @param board The board of the game.
     * @return The new board after the capture.
     */
    override fun capture(from: Square, to: Square, board: Board): Moves{
        val midRow = (from.row.index + to.row.index) / 2
        val midCol = (from.column.index + to.column.index) / 2
        val midSquare = Square(Row.values[midRow], Column.values[midCol])
        return board.moves + (to to this) + (from to null) + (midSquare to null)
    }

}

