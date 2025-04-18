package isel.leic.tds.checkers.model

import kotlin.collections.plus

open class Piece(val player: Player) {
    val symbol: Char = when (player) {
        Player.WHITE -> 'w'
        Player.BLACK -> 'b'
    }

    /**
     * Representation of the piece
     * @return the symbol of the piece as a string
     */
    override fun toString(): String {
        return symbol.toString()
    }

    /**
     * Verificar se o square ao qual se quer mover args[2] = to está com o Player '-'
     * Caso esteja temos que verificar se em termos de regras de jogo essa jogada pode ser feito
     * ou não.
     */
    open fun canMove(from: Square, to: Square, board: Board): Boolean {
        return false // tendo em conta que vamos redefinir na sublaclasse
    }

    open fun canCapture(from: Square, to: Square, board: Board): Boolean {
        return false // tendo em conta que vamos redefinir na sublaclasse
        // é open para garantir que podemos redefinir na subclasse e se n for apenas n retorna
    }

    open fun capture(from: Square, to: Square, board: Board): Moves {
        return emptyMap()
    }

    /**
     * Verificar se o square ao qual se quer mover args[2] = to tem uma peça do inimigo e que o possa
     * capturar
     * Caso esteja temos que verificar se em termos de regras de jogo essa jogada pode ser feito
     * ou não.
     */

}

/**
 * Verifies if when a piece moves to a square it is promoted
 * @param to the square to which the piece is moving
 * @return true if the piece is promoted, false otherwise
 */
fun Piece.verifyPromotionOfPiece(to: Square): Boolean {
    return when (player) {
        Player.WHITE -> to.row.index == 0
        Player.BLACK -> to.row.index == BOARD_DIM - 1
    }
}

/**
 * Updates the moves with the promotion of the piece
 * @param to the square to which the piece is moving
 * @param moves the moves on the board
 * @return the updated moves with the promotion of the piece
 */
fun Piece.updatePiece(to: Square, moves: Moves): Moves{
   return if (this.verifyPromotionOfPiece(to)) {
        moves + (to to Queen(this.player))
    } else {
        moves
    }
}