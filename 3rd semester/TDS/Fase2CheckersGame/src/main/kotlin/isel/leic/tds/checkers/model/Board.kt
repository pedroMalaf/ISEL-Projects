package isel.leic.tds.checkers.model
import androidx.compose.runtime.Composable
import kotlin.collections.plus
import isel.leic.tds.storage.Serializer
import kotlin.collections.plusAssign
import kotlin.text.get


// Constant that keeps the value of the dimension of the board
const val BOARD_DIM = 8

// Typealias that represents a move
typealias Move = Pair<Square, Piece?>
// Typealias that represents all the moves on the board
typealias Moves = Map<Square, Piece?>

/**
 * Represents the board of the game
 * @property moves represents the pieces on the board
 * @property playerTurn represents the player that has the turn
 */
sealed class Board (
    val moves: Moves,
    val playerTurn: Player
){
    override fun equals(other: Any?) = other is Board && moves == other.moves
    override fun hashCode(): Int = moves.hashCode()
}
/**
 * Represents the board of a game that is still running
 * @property mvs represents the pieces on the board
 * @property turn represents the player that has the turn
 */
class BoardRun(mvs: Moves, val turn: Player): Board(mvs, turn)

/**
 * Represents the board of a game that has finished
 * @property mvs represents the pieces on the board
 * @property winner represents the player that has won the game
 */
class BoardFinish(mvs: Moves, val winner: Player): Board(mvs, winner)

/**
 * Serializer for the Board class
 */
object BoardSerializer: Serializer<Board>{

    /**
     * Serializes the board data to a string
     * @param data the board to serialize
     * @return the serialized board string
     */
    override fun serialize(data: Board): String {

        //val moves = data.moves.filter{it.value != null}.map { "${it.key}:${it.value}" }.joinToString(" ")
        val moves = data.moves.filter { it.value != null }.map { (square, piece) -> val pieceType = if (piece is Queen) {
                if (piece.player.symbol == 'w') 'W' else 'B'
            } else {
                if (piece?.player?.symbol == 'w') 'w' else 'b'
            }
            "$square:$pieceType"
        }.joinToString(" ")

        return when(data){
            is BoardRun -> "RUN ${data.turn.symbol}"
            is BoardFinish -> "END ${data.winner.symbol}"
        } + " | $moves"
    }

    /**
     * Deserializes the board data from a string
     * @param text the string to deserialize
     * @return the deserialized board
     */
    override fun deserialize(text: String): Board {

        val (state, plays) = text.split(" | ")
        val (type, player) = state.split(" ")

        val moves = if(plays.isEmpty()) emptyMap()
        else plays.split(" ").associate {
            val (square, play) = it.split(":")

            if(play.toCharArray()[0].isUpperCase()) {
                square.toSquare() to Queen(playerCreate(play.replace("\n", "")))
            }
            else {
                square.toSquare() to Pawn(playerCreate(play.replace("\n", "")))
                }
        }
        return when(type){
            "RUN" -> BoardRun(moves, playerCreate(player))
            "END" -> BoardFinish(moves, playerCreate(player))
            else -> error("Invalid state $type")
        }
    }
}

/**
 * Function that returns a map of the initial composition of the board
 * @return a map with the initial composition of the board
 */
fun initialBoardComposition(): Map<Square, Piece?> {
    require(BOARD_DIM in 4..8 step 2) { "Invalid board dimension" }
    return Square.blackSquares.associateWith {
        when (it.row.index){
            in 0 until BOARD_DIM / 2 - 1 -> Pawn(Player.BLACK) // Black pieces in the first rows
            in BOARD_DIM / 2 + 1 until BOARD_DIM -> Pawn(Player.WHITE) // White pieces in the last rows
            else -> null // empty squares in the middle
        }
    }
}

/**
 * Function that initializes the board
 * @return a BoardRun with the initial composition of the board and the player that has the turn
 */
fun initBoard(): Board = BoardRun(initialBoardComposition(), Player.WHITE)

/**
 *
 * @param from the square the piece is at
 * @param to the square you want to move to
 * @return a new board with the updated moves
 */
fun Board.play(from: Square, to: Square): Board = when(this){
    is BoardFinish -> throw IllegalStateException("Game is over")
    is BoardRun -> {
        require(moves[from]?.player == playerTurn) { "You are only allowed to move the $playerTurn pieces" }
        require(moves[to] == null) { "Square $to is not empty" }
        require(moves[from] != null) { "No piece at $from" }
        move(from, to)
    }
}


/**
 * Makes a move from one square to another
 * @param from the square the piece is at
 * @param to the square you want to move to
 * @return a new board with the updated moves
 */
fun BoardRun.move(from: Square, to: Square): Board {
    val piece = moves[from] ?: throw IllegalArgumentException("No piece at $from")

    // Verifies if there are available captures
    val capturesAvailable = checkCapturesAvailable().any { it.value }

    if (capturesAvailable) {
        if (piece.canCapture(from, to, this)) {
            val updatedMoves = piece.capture(from, to, this)
            val updatePiece = piece.updatePiece(to, updatedMoves)
            val newBoard = if (additionalCapturesAvailable(to, updatePiece)) {
                BoardRun(updatePiece, playerTurn)
            } else {
                BoardRun(updatePiece, playerTurn.other)
            }
            val checkFinish = newBoard.checkFinish()
            return if (checkFinish) {
                BoardFinish(newBoard.moves, playerTurn)
            } else {
                newBoard
            }
        } else {
            throw IllegalArgumentException("Capture is mandatory")
        }
    } else {
        if (piece.canMove(from, to, this)) {
            val updatedMoves = moves + (to to piece) + (from to null)
            val updatePiece = piece.updatePiece(to, updatedMoves)
            val newBoard = BoardRun(updatePiece, playerTurn.other)
            val checkFinish = newBoard.checkFinish()
            return if (checkFinish) {
                BoardFinish(newBoard.moves, playerTurn)
            } else {
                newBoard
            }
        } else {
            throw IllegalArgumentException("Invalid move")
        }
    }
}

/**
 * Checks if the piece that just moved has additional captures available
 * @param from the square the piece is at
 * @param moves the moves on the board
 * @return true if there are additional captures available, false otherwise
 */
fun BoardRun.additionalCapturesAvailable(from: Square, moves: Moves): Boolean { // mudar estas merdas de sitio
    val piece = moves[from] ?: return false
    val directions = listOf(
        Pair(-2, -2), Pair(-2, 2), // Up-left, Up-right
        Pair(2, -2), Pair(2, 2)    // Down-left, Down-right
    )
    for (direction in directions) {
        val toRow = from.row.index + direction.first
        val toCol = from.column.index + direction.second
        if (toRow in 0 until BOARD_DIM && toCol in 0 until BOARD_DIM) {
            if (piece.canCapture(from, Square(Row(toRow), Column(toCol)), this)) {
                return true
            }
        }
    }
    return false
}

/**
 * Checks if there are captures available
 * @return a map with the squares and a boolean that represents if there are captures available
 */
fun BoardRun.checkCapturesAvailable(): Map<Square, Boolean> {
    return moves.filter { it.value?.player == playerTurn }.mapValues { (square, piece) ->
        piece?.let {
            val directions = listOf(
                Pair(-1, -1), Pair(-1, 1), // Up-left, Up-right
                Pair(1, -1), Pair(1, 1)    // Down-left, Down-right
            )
            directions.any { (rowStep, colStep) ->
                var toRow = square.row.index + rowStep
                var toCol = square.column.index + colStep
                var enemyFound = false

                while (toRow in 0 until BOARD_DIM && toCol in 0 until BOARD_DIM) {
                    val toSquare = Square(Row(toRow), Column(toCol))
                    val targetPiece = moves[toSquare]

                    if (targetPiece == null) {
                        if (enemyFound && piece.canCapture(square, toSquare, this)) {
                            return@any true
                        }
                    } else {
                        if (targetPiece.player != piece.player) {
                            if (!enemyFound) {
                                enemyFound = true
                            } else {
                                break
                            }
                        } else {
                            break
                        }
                    }

                    toRow += rowStep
                    toCol += colStep
                }
                false
            }
        } == true
    }
}

/**
 * Checks if the game has finished
 * @return true if the game has finished, false otherwise
 */
fun BoardRun.checkFinish(): Boolean{
    val checkPieces = this.moves.values.mapNotNull { it?.symbol }.toSet()
    return 'w' !in checkPieces || 'b' !in checkPieces
}

/**
 * Function that returns the possible moves for a pawn
 * @param from the square the pawn is at
 * @return a list with the possible moves for the pawn
 */
fun BoardRun.possibleMovesForPawn(from: Square): List<Square> {
    val piece = moves[from] ?: return emptyList()
    if (piece !is Pawn) return emptyList()

    val captureDirections = listOf(
        Pair(-2, -2), Pair(-2, 2), // Up-left, Up-right
        Pair(2, -2), Pair(2, 2)    // Down-left, Down-right
    )

    val captureMoves = captureDirections.mapNotNull { (rowDiff, colDiff) ->
        val toRow = from.row.index + rowDiff
        val toCol = from.column.index + colDiff
        if (toRow in 0 until BOARD_DIM && toCol in 0 until BOARD_DIM) {
            val toSquare = Square(Row(toRow), Column(toCol))
            if (piece.canCapture(from, toSquare, this)) toSquare else null
        } else null
    }

    if (captureMoves.isNotEmpty()) return captureMoves

    val normalDirections = if (piece.player == Player.WHITE) {
        listOf(Pair(-1, -1), Pair(-1, 1)) // Up-left, Up-right for white pawns
    } else {
        listOf(Pair(1, -1), Pair(1, 1)) // Down-left, Down-right for black pawns
    }

    return normalDirections.mapNotNull { (rowDiff, colDiff) ->
        val toRow = from.row.index + rowDiff
        val toCol = from.column.index + colDiff
        if (toRow in 0 until BOARD_DIM && toCol in 0 until BOARD_DIM) {
            val toSquare = Square(Row(toRow), Column(toCol))
            if (moves[toSquare] == null) toSquare else null
        } else null
    }
}

/**
 * Function that returns the possible moves for a queen
 * @param from the square the queen is at
 * @return a list with the possible moves for the queen
 */
fun BoardRun.possibleMovesForQueen(from: Square): List<Square> {
    val piece = moves[from] ?: return emptyList()
    if (piece !is Queen) return emptyList()

    val directions = listOf(
        Pair(1, 1), Pair(1, -1), // Down-right, Down-left
        Pair(-1, 1), Pair(-1, -1) // Up-right, Up-left
    )

    val possibleMoves = mutableListOf<Square>()
    val captureMoves = mutableListOf<Square>()

    directions.forEach { (rowStep, colStep) ->
        var toRow = from.row.index + rowStep
        var toCol = from.column.index + colStep
        var enemyFound = false

        while (toRow in 0 until BOARD_DIM && toCol in 0 until BOARD_DIM) {
            val toSquare = Square(Row(toRow), Column(toCol))
            val targetPiece = moves[toSquare]

            if (targetPiece == null) {
                if (enemyFound) {
                    captureMoves.add(toSquare)
                    break
                } else {
                    possibleMoves.add(toSquare)
                }
            } else {
                if (targetPiece.player != piece.player) {
                    if (!enemyFound) {
                        enemyFound = true
                    } else {
                        break
                    }
                } else {
                    break
                }
            }

            toRow += rowStep
            toCol += colStep
        }
    }

    return if (captureMoves.isNotEmpty()) captureMoves else possibleMoves
}